package com.fitnesstracker.model;

public class HealthResponse
{
    private Boolean isUp;

    public HealthResponse(Boolean isUp)
    {
        this.isUp = isUp;
    }

    public Boolean getIsUp()
    {
        return this.isUp;
    }

    public void setIsUp(Boolean up)
    {
        this.isUp = up;
    }
}
