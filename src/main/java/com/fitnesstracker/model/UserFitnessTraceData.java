package com.fitnesstracker.model;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "user_fitness_traces")
public class UserFitnessTraceData
{
    @Id
    private String id;
    private String userId;
    private int steps;
    private int heartBeat;
    private double met;
    private Instant timestamp;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public int getSteps()
    {
        return steps;
    }

    public void setSteps(int steps)
    {
        this.steps = steps;
    }

    public int getHeartBeat()
    {
        return heartBeat;
    }

    public void setHeartBeat(int heartBeat)
    {
        this.heartBeat = heartBeat;
    }

    public double getMet()
    {
        return met;
    }

    public void setMet(double met)
    {
        this.met = met;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp)
    {
        this.timestamp = timestamp;
    }

    @Override
    public String toString()
    {
        return "UserFitnessTraceData{" +
                ", userId='" + userId + '\'' +
                ", steps=" + steps +
                ", heartBeat=" + heartBeat +
                ", met=" + met +
                ", timestamp=" + timestamp +
                '}';
    }
}
