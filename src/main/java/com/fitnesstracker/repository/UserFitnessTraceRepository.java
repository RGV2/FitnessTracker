package com.fitnesstracker.repository;

import com.fitnesstracker.model.UserFitnessTraceData;

import java.time.Instant;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;


public interface UserFitnessTraceRepository extends ReactiveMongoRepository<UserFitnessTraceData, String>
{
    Flux<UserFitnessTraceData> findByUserIdAndTimestampBetween(String userId, Instant startTime, Instant endTime);
}
