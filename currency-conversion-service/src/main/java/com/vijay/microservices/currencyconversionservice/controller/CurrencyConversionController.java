package com.vijay.microservices.currencyconversionservice.controller;

import com.vijay.microservices.currencyconversionservice.bean.CurrencyConversion;
import com.vijay.microservices.currencyconversionservice.proxy.CurrencyExchangeProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
public class CurrencyConversionController {

    Logger logger = LoggerFactory.getLogger(CurrencyConversionController.class);

    @Autowired
    private CurrencyExchangeProxy currencyExchangeProxy;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversion(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity){
        logger.info("calculateCurrencyConversion from {} to {} quantity {}",from,to,quantity);
        HashMap<String,String> uriVariables = new HashMap<>();
        uriVariables.put("from",from);
        uriVariables.put("to",to);
        ResponseEntity<CurrencyConversion> responseEntity = restTemplate.getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversion.class, uriVariables);
        CurrencyConversion currencyConversion = responseEntity.getBody();
        return new CurrencyConversion(currencyConversion.getId(),from,to, currencyConversion.getConversionMultiple(),quantity
                ,quantity.multiply(currencyConversion.getConversionMultiple()),currencyConversion.getEnvironment());
    }

    @GetMapping("currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversionFeign(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity){

        CurrencyConversion currencyConversion = currencyExchangeProxy.retrieveExchangeValue(from, to);

        return new CurrencyConversion(currencyConversion.getId(),from,to, currencyConversion.getConversionMultiple(),quantity
                ,quantity.multiply(currencyConversion.getConversionMultiple()),currencyConversion.getEnvironment());
    }
}
