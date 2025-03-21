package com.fitnesstracker.model;

public class TokenRequest
{
    private String clientId;
    private String clientSecret;

    public TokenRequest(String clientId, String clientSecret)
    {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getClientId()
    {
        return clientId;
    }

    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    public String getClientSecret()
    {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret)
    {
        this.clientSecret = clientSecret;
    }
}