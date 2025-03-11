package com.fitnesstracker.service.sub.impl;

import com.fitnesstracker.model.UserFitnessTraceData;
import com.fitnesstracker.repository.UserFitnessTraceRepository;

import com.fitnesstracker.service.sub.FitnessTrackerUserTracesService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;


@Service
public class FitnessTrackerFitnessTrackerUserTracesServiceImpl implements FitnessTrackerUserTracesService
{
    private final UserFitnessTraceRepository userFitnessTraceRepository;

    public FitnessTrackerFitnessTrackerUserTracesServiceImpl(UserFitnessTraceRepository userFitnessTraceRepository)
    {
        this.userFitnessTraceRepository = userFitnessTraceRepository;
    }

    @Override
    public Flux<UserFitnessTraceData> getFitnessTraceData(String userId, String date)
    {
        LocalDate requestedDate = parseDate(date).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Instant startOfDay = requestedDate.atStartOfDay(ZoneId.of("UTC")).toInstant();
        Instant endOfDay = requestedDate.plusDays(1).atStartOfDay(ZoneId.of("UTC")).toInstant().minusNanos(1);

        Flux<UserFitnessTraceData> userFitnessTraces = userFitnessTraceRepository.findByUserIdAndTimestampBetween(userId, startOfDay, endOfDay);
        System.out.println("UserFitnessTraceData received of size: " + userFitnessTraces.count().block());

        return userFitnessTraces;
    }

    private Date parseDate(String date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try
        {
            return sdf.parse(date);
        }
        catch (ParseException e)
        {
            return new Date();
        }
    }
}