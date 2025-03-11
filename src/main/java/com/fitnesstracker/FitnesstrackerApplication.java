package com.fitnesstracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;


@SpringBootApplication
@EnableReactiveMongoRepositories(basePackages = "com.fitnesstracker.repository")
public class FitnesstrackerApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(FitnesstrackerApplication.class, args);
	}

}
