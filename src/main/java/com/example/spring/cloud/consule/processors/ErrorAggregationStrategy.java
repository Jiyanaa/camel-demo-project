package com.example.spring.cloud.consule.processors;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.spring.cloud.consule.exception.model.ErrorResponse;

@Component
public class ErrorAggregationStrategy implements AggregationStrategy {

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            oldExchange = newExchange;
        }
        else {
            ErrorResponse newErrorResponseModel = newExchange.getIn().getBody(ErrorResponse.class);
            if(newErrorResponseModel != null)
                oldExchange.getIn().setBody(newErrorResponseModel);
        }
        return oldExchange;
    }

    private void setSuccessHeaders(Exchange exchange) {
        exchange.getIn().removeHeaders("*");
        exchange.getIn().setHeader("CamelHttpResponseCode", HttpStatus.OK.value());
        exchange.getIn().setHeader("CamelHttpResponseText", HttpStatus.OK.getReasonPhrase());
    }

}