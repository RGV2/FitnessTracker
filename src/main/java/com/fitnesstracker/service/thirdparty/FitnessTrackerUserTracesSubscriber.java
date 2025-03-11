package com.fitnesstracker.service.thirdparty;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitnesstracker.model.UserFitnessTraceData;
import com.fitnesstracker.repository.UserFitnessTraceRepository;

import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;


@Service
public class FitnessTrackerUserTracesSubscriber
{
    private final UserFitnessTraceRepository repository;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public FitnessTrackerUserTracesSubscriber(UserFitnessTraceRepository repository,
                                              TokenService tokenService,
                                              ObjectMapper objectMapper,
                                              WebClient.Builder webClientBuilder,
                                              @Value("${fitness.tracker.publisher.baseUrl}")
                                                 String publisherBaseUrl)
    {
        this.repository = repository;
        this.tokenService = tokenService;
        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder.baseUrl(publisherBaseUrl).build();
    }

    @PostConstruct
    public void connect()
    {
        isServerUp()
                .flatMap(this::handleServerStatus)
                .flatMap(this::connectWebSocket)
                .retryWhen(Retry.fixedDelay(Long.MAX_VALUE, Duration.ofMinutes(1)))
                .subscribe();
    }

    private Mono<Boolean> isServerUp()
    {
        return webClient.get()
                .uri("/api/v1/health")
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> response.contains("\"isUp\":true"))
                .defaultIfEmpty(false)
                .onErrorReturn(false);
    }

    private Mono<String> handleServerStatus(Boolean isUp)
    {
        if (isUp)
        {
            System.out.println("Fit Tracker Publisher is up & running, connecting...");
            return tokenService.getThirdPartyToken();
        }
        else
        {
            System.out.println("Fit Tracker Server is down, will retry after 1 min.");
            return Mono.error(new RuntimeException("Server is down"));
        }
    }

    private Mono<Void> connectWebSocket(String token)
    {
        HttpClient httpClient = HttpClient.create().headers(headers -> headers.add(HttpHeaders.AUTHORIZATION, token));
        ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient(httpClient);

        return client.execute(
                URI.create("wss://fit-tracker-htmz.onrender.com/api/v1/traces"),
                session -> session.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .flatMap(this::processUserFitnessTraceData)
                        .then()
        );
    }

    private Mono<Void> processUserFitnessTraceData(String traceData)
    {
        try
        {
            JsonNode rawJsonData = objectMapper.readTree(decodeBase64(traceData));
            UserFitnessTraceData data = new UserFitnessTraceData();
            data.setUserId(rawJsonData.get("userId").asText());
            data.setSteps(rawJsonData.get("steps").asInt());
            data.setHeartBeat(rawJsonData.get("heartBeat").asInt());
            data.setMet(rawJsonData.get("met").asDouble());
            data.setTimestamp(Instant.now());
            System.out.println("Saving received UserFitnessTraceData to DB: " + data);
            return repository.save(data).then();
        }
        catch (Exception e)
        {
            System.err.println("Error processing UserFitnessTraceData: " + e);
            return Mono.empty();
        }
    }

    private String decodeBase64(String base64Message)
    {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Message);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}
