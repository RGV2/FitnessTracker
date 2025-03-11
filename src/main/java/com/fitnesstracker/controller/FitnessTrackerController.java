package com.fitnesstracker.controller;

import com.fitnesstracker.facade.FitnessTrackerFacade;
import com.fitnesstracker.facade.impl.FitnessTrackerFacadeImpl;
import com.fitnesstracker.model.ErrorMessage;
import com.fitnesstracker.model.FitnessInsightResponse;
import com.fitnesstracker.model.HealthResponse;
import com.fitnesstracker.model.TokenRequest;
import com.fitnesstracker.model.TokenResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;


@RestController
@RequestMapping("subs/api/v1")
@Tag(name = "fitness-tracker-subscriber", description = "Access fitness-tracker subscriber data")
public class FitnessTrackerController
{
    private final FitnessTrackerFacade fitnessTrackerFacade;

    public FitnessTrackerController(FitnessTrackerFacadeImpl fitnessTrackerFacade)
    {
        this.fitnessTrackerFacade = fitnessTrackerFacade;
    }

    @RequestMapping("/")
    public void redirect(HttpServletResponse response) throws IOException
    {
        response.sendRedirect("/swagger-ui.html");
    }

    @GetMapping("/health")
    @Operation(summary = "Get fitness tracker server health status")
    public ResponseEntity<HealthResponse> getHealthStatus()
    {
        return ResponseEntity.status(HttpStatus.OK).body(new HealthResponse(Boolean.TRUE));
    }

    @PostMapping("/token")
    @Operation(summary = "Generate token for fitness tracker")
    public ResponseEntity<?> generateTokenForFitnessTracker(@RequestBody TokenRequest tokenRequest)
    {
        String clientId = tokenRequest.getClientId();
        String clientSecret = tokenRequest.getClientSecret();

        if (fitnessTrackerFacade.validateCredentials(clientId, clientSecret))
        {
            TokenResponse response = fitnessTrackerFacade.getTokenResponse(clientId);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        else
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessage("Invalid Credentials."));
        }
    }

    @GetMapping("/insights")
    @Operation(summary = "Get user fitness insight for the requested date (Format: yyyy-MM-dd)")
    @SecurityRequirement(name = "bearerAuth")
    public Mono<ResponseEntity<?>> getFitnessTraceData(@RequestParam("userId") String userId, @RequestParam("date") String date)
    {
        ErrorMessage errorMessage = fitnessTrackerFacade.validateParam(userId, date);
        if (Objects.nonNull(errorMessage))
        {
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage));
        }

        Mono<FitnessInsightResponse> userInsights = fitnessTrackerFacade.getUserInsights(userId, date);

        if (Objects.nonNull(userInsights))
        {
            return Mono.just(ResponseEntity.status(HttpStatus.OK).body(userInsights.block()));
        }

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("No Data Found!")));
    }

    @GetMapping("/rawTraceData")
    @Operation(summary = "Get user raw fitness trace data for the requested date in format yyyy-MM-dd")
    @SecurityRequirement(name = "bearerAuth")
    public Mono<ResponseEntity<?>> getRawFitnessTraceData(@RequestParam("userId") String userId, @RequestParam("date") String date)
    {
        ErrorMessage errorMessage = fitnessTrackerFacade.validateParam(userId, date);
        if (Objects.nonNull(errorMessage))
        {
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage));
        }

        return fitnessTrackerFacade.getFitnessTraceData(userId, date).collectList()
                .flatMap(data ->
                {
                    if (data.isEmpty())
                    {
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("No Data Found!")));
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.OK).body(data));
                });
    }

}