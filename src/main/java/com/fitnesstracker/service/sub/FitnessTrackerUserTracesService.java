package com.fitnesstracker.service.sub;

import com.fitnesstracker.model.UserFitnessTraceData;

import reactor.core.publisher.Flux;


public interface FitnessTrackerUserTracesService
{
    Flux<UserFitnessTraceData> getFitnessTraceData(String userId, String date);
}
