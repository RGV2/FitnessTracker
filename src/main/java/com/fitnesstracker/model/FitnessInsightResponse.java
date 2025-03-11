package com.fitnesstracker.model;

public class FitnessInsightResponse
{
    private String userId;
    private int totalSteps;
    private double distance;
    private double avgHeartBeat;
    private double kcal;

    public FitnessInsightResponse(String userId, int totalSteps, double distance, double avgHeartBeat, double kcal)
    {
        this.userId = userId;
        this.totalSteps = totalSteps;
        this.distance = distance;
        this.avgHeartBeat = avgHeartBeat;
        this.kcal = kcal;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public int getTotalSteps()
    {
        return totalSteps;
    }

    public void setTotalSteps(int totalSteps)
    {
        this.totalSteps = totalSteps;
    }

    public double getDistance()
    {
        return distance;
    }

    public void setDistance(double distance)
    {
        this.distance = distance;
    }

    public double getAvgHeartBeat()
    {
        return avgHeartBeat;
    }

    public void setAvgHeartBeat(double avgHeartBeat)
    {
        this.avgHeartBeat = avgHeartBeat;
    }

    public double getKcal()
    {
        return kcal;
    }

    public void setKcal(double kcal)
    {
        this.kcal = kcal;
    }
}
