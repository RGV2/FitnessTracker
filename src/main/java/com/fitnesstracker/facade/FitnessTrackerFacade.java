package com.fitnesstracker.facade;

import com.fitnesstracker.model.ErrorMessage;
import com.fitnesstracker.model.FitnessInsightResponse;
import com.fitnesstracker.model.TokenResponse;
import com.fitnesstracker.model.UserFitnessTraceData;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface FitnessTrackerFacade
{
    Boolean validateCredentials(String requestClientId, String requestClientSecret);

    TokenResponse getTokenResponse(String requestClientId);

    ErrorMessage validateParam(String userId, String date);

    Flux<UserFitnessTraceData> getFitnessTraceData(String userId, String date);

    Mono<FitnessInsightResponse> getUserInsights(String userId, String date);
}
