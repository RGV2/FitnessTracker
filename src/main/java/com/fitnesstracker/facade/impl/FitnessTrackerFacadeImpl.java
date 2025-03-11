package com.fitnesstracker.facade.impl;

import com.fitnesstracker.facade.FitnessTrackerFacade;
import com.fitnesstracker.model.ErrorMessage;
import com.fitnesstracker.model.FitnessInsightResponse;
import com.fitnesstracker.model.TokenResponse;
import com.fitnesstracker.model.UserFitnessTraceData;
import com.fitnesstracker.model.UserResponse;
import com.fitnesstracker.service.sub.FitnessTrackerUserTracesService;
import com.fitnesstracker.service.sub.impl.FitnessTrackerSecurityServiceImpl;
import com.fitnesstracker.service.sub.impl.FitnessTrackerFitnessTrackerUserTracesServiceImpl;
import com.fitnesstracker.service.thirdparty.FitnessTrackerUserService;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class FitnessTrackerFacadeImpl implements FitnessTrackerFacade
{
    private final FitnessTrackerUserTracesService userTracesService;
    private final FitnessTrackerSecurityServiceImpl securityService;
    private final FitnessTrackerUserService fitnessTrackerUserService;

    public FitnessTrackerFacadeImpl(FitnessTrackerFitnessTrackerUserTracesServiceImpl userTracesService,
                                    FitnessTrackerSecurityServiceImpl securityService,
                                    FitnessTrackerUserService fitnessTrackerUserService)
    {
        this.userTracesService = userTracesService;
        this.securityService = securityService;
        this.fitnessTrackerUserService = fitnessTrackerUserService;
    }

    @Override
    public Boolean validateCredentials(String requestClientId, String requestClientSecret)
    {
        if (Objects.nonNull(requestClientId) && Objects.nonNull(requestClientSecret))
        {
            return securityService.validateCredentials(requestClientId, requestClientSecret);
        }
        return Boolean.FALSE;
    }

    @Override
    public TokenResponse getTokenResponse(String requestClientId)
    {
        TokenResponse response = new TokenResponse();
        String token = securityService.generateTokenForFitnessTracker(requestClientId);
        response.setAccessToken(token);
        return response;
    }

    @Override
    public ErrorMessage validateParam(String userId, String date)
    {
        if (StringUtils.isEmpty(userId) || !userId.matches("^[a-zA-Z0-9]*$"))
        {
            return new ErrorMessage("Invalid user ID format. Expected: alphanumeric.");
        }
        if (StringUtils.isEmpty(date) || !date.matches("^\\d{4}-\\d{2}-\\d{2}$"))
        {
            return new ErrorMessage("Invalid date format. Expected: yyyy-MM-dd");
        }
        return null;
    }

    @Override
    public Flux<UserFitnessTraceData> getFitnessTraceData(String userId, String date)
    {
        if (StringUtils.isNoneEmpty(userId))
        {
            return userTracesService.getFitnessTraceData(userId, date);
        }
        return null;
    }

    @Override
    public Mono<FitnessInsightResponse> getUserInsights(String userId, String date)
    {
        Mono<UserResponse> userResponse = fitnessTrackerUserService.fetchUserData(userId);
        if (Boolean.TRUE.equals(userResponse.hasElement().block()))
        {
            int userWeight = getUserWeight(userResponse);

            Flux<UserFitnessTraceData> fitnessTraceData = userTracesService.getFitnessTraceData(userId, date);

            return processFitnessRecords(userId, userWeight, fitnessTraceData);
        }
        return null;
    }

    private int getUserWeight(Mono<UserResponse> userResponseMono)
    {
        return Objects.requireNonNull(userResponseMono.block()).getWeight();
    }

    private Mono<FitnessInsightResponse> processFitnessRecords(String userId, int userWeight, Flux<UserFitnessTraceData> records)
    {
        return records.collectList()
                .flatMap(list ->
                {
                    if (list.isEmpty())
                    {
                        return Mono.just(new FitnessInsightResponse("",0, 0.0, 0.0, 0.0));
                    }

                    int totalSteps = list.stream().mapToInt(UserFitnessTraceData::getSteps).sum();
                    double distance = (totalSteps / 1000.0) * 0.7;
                    double avgHeartBeat = list.stream().mapToInt(UserFitnessTraceData::getHeartBeat).average().orElse(0);
                    double avgMET = list.stream().mapToDouble(UserFitnessTraceData::getMet).average().orElse(0);
                    double kcal = list.size() * (avgMET * 3.5 * userWeight / 200);

                    return Mono.just(new FitnessInsightResponse(userId, totalSteps, distance, avgHeartBeat, kcal));
                });
    }

}
