package com.fitnesstracker.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitnesstracker.service.sub.FitnessTrackerSecurityService;
import com.fitnesstracker.service.sub.impl.FitnessTrackerSecurityServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class FitnessTrackerJwtAuthInterceptor implements HandlerInterceptor
{
    private final FitnessTrackerSecurityService fitnessTrackerSecurityService;

    public FitnessTrackerJwtAuthInterceptor(FitnessTrackerSecurityServiceImpl fitnessTrackerSecurityService)
    {
        this.fitnessTrackerSecurityService = fitnessTrackerSecurityService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException
    {
        String token = request.getHeader("Authorization");

        if (Objects.isNull(token) || !fitnessTrackerSecurityService.validateToken(token))
        {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            Map<String, String> errorResponse = Map.of("message", "Unauthorized: Invalid or missing token");

            ObjectMapper objectMapper = new ObjectMapper();
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));

            return false;
        }
        return true;
    }
}