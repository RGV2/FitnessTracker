package com.fitnesstracker.service.sub.impl;

import com.fitnesstracker.service.sub.FitnessTrackerSecurityService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.util.Date;

import javax.crypto.SecretKey;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class FitnessTrackerSecurityServiceImpl implements FitnessTrackerSecurityService
{
    @Value("${fitness.tracker.subscriber.clientId}")
    private String subscriberClientId;

    @Value("${fitness.tracker.subscriber.clientSecret}")
    private String subscriberClientSecret;

    @Value("${fitness.tracker.subscriber.tokenSecret}")
    private String subscriberTokenSecret;

    @Value("${fitness.tracker.subscriber.tokenExpirationMs}")
    private long subscriberTokenExpirationMs;

    @Override
    public Boolean validateCredentials(String requestClientId, String requestClientSecret)
    {
        if (subscriberClientId.equals(requestClientId) && subscriberClientSecret.equals(requestClientSecret))
        {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public String generateTokenForFitnessTracker(String clientId)
    {
        return Jwts.builder()
                .subject(clientId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + subscriberTokenExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Boolean validateToken(String token)
    {
        if (StringUtils.isNoneEmpty(token) && token.startsWith("Bearer"))
        {
            try
            {
                Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token.substring(7));
                return Boolean.TRUE;
            }
            catch (Exception e)
            {
                return Boolean.FALSE;
            }
        }
        else
        {
            return Boolean.FALSE;
        }
    }

    private SecretKey getSigningKey()
    {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(subscriberTokenSecret));
    }
}
