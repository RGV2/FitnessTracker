package com.fitnesstracker.service.thirdparty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitnesstracker.model.UserResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;


@Service
public class FitnessTrackerUserService
{
    private final TokenService tokenService;
    private final WebClient webClient;


    public FitnessTrackerUserService(@Value("${fitness.tracker.publisher.baseUrl}") String publisherBaseUrl,
                                     WebClient.Builder webClientBuilder,
                                     TokenService tokenService)
    {
        this.tokenService = tokenService;
        this.webClient = webClientBuilder.baseUrl(publisherBaseUrl).build();
    }

    public Mono<UserResponse> fetchUserData(String userId)
    {
        return tokenService.getThirdPartyToken()
                .flatMap(token -> webClient.get()
                        .uri("/api/v1/users/{userId}", userId)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .retrieve()
                        .bodyToMono(String.class)
                        .flatMap(response -> parseUserResponse(response, userId))
                        .onErrorResume(error -> {
                            System.err.println("Error fetching user data for userId " + userId + ": " + error.getMessage());
                            return Mono.empty();
                        }));
    }

    private Mono<UserResponse> parseUserResponse(String response, String userId)
    {
        try
        {
            System.out.println("Raw User API Response for " + userId + ": " + response);
            ObjectMapper objectMapper = new ObjectMapper();
            return Mono.just(objectMapper.readValue(response, UserResponse.class));
        }
        catch (JsonProcessingException e)
        {
            System.err.println("Error parsing user data for userId " + userId + ": " + e.getMessage());
            return Mono.empty();
        }
    }
}
