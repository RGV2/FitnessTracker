package com.fitnesstracker.service.thirdparty;

import com.fitnesstracker.model.TokenRequest;
import com.fitnesstracker.model.TokenResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;


@Service
public class TokenService
{
    @Value("${fitness.tracker.publisher.clientId}")
    private String publisherClientId;

    @Value("${fitness.tracker.publisher.clientSecret}")
    private String publisherClientSecret;

    private final WebClient webClient;

    public TokenService(@Value("${fitness.tracker.publisher.baseUrl}") String publisherBaseUrl, WebClient.Builder webClientBuilder)
    {
        this.webClient = webClientBuilder.baseUrl(publisherBaseUrl).build();
    }

    public Mono<String> getThirdPartyToken()
    {
        return webClient.post()
                .uri("/api/v1/token")
                .bodyValue(new TokenRequest(publisherClientId, publisherClientSecret))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .map(TokenResponse::getAccessToken);
    }

}
