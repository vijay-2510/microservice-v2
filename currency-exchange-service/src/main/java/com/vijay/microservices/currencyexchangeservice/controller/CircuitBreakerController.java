package com.vijay.microservices.currencyexchangeservice.controller;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CircuitBreakerController {

    private Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);

    @GetMapping("/sample-api")
    //@Retry(name = "sample-api",fallbackMethod = "sampleResponse")
    @Bulkhead(name="default")
    public String sampleApi(){
        logger.info("Sample-api call received");
        ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:8080/dummy-api", String.class);

        return forEntity.getBody();
    }

    @GetMapping("/sample-api-circuitbreaker")
    //@CircuitBreaker(name = "default", fallbackMethod = "sampleResponse")
    @RateLimiter(name="default")
    public String circuitBreakerApi(){
        logger.info("sample-api-circuitbreaker call received");
        ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:8080/dummy-api", String.class);

        return forEntity.getBody();
    }

    public String sampleResponse(Exception ex){
        return "Sample fallback response";
    }
}
