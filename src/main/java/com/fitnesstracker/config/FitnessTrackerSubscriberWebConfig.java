package com.fitnesstracker.config;

import com.fitnesstracker.security.FitnessTrackerJwtAuthInterceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class FitnessTrackerSubscriberWebConfig implements WebMvcConfigurer
{
    @Autowired
    private FitnessTrackerJwtAuthInterceptor fitnessTrackerJwtAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(fitnessTrackerJwtAuthInterceptor).addPathPatterns("/subs/api/v1/rawTraceData/*", "/subs/api/v1/insights");
    }
}