package com.fitnesstracker.service.sub;

public interface FitnessTrackerSecurityService
{
    Boolean validateCredentials(String clientId, String clientSecret);

    String generateTokenForFitnessTracker(String clientId);

    Boolean validateToken(String token);
}
