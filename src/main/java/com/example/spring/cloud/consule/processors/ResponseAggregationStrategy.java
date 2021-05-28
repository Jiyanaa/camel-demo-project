package com.example.spring.cloud.consule.processors;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.spring.cloud.consule.model.SuccessResponseModel;


@Component
public class ResponseAggregationStrategy implements AggregationStrategy {
    private Logger logger = LoggerFactory.getLogger(ResponseAggregationStrategy.class);

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            oldExchange = newExchange;
        }
        else {
            SuccessResponseModel newResponseModel = newExchange.getIn().getBody(SuccessResponseModel.class);
            if(newResponseModel != null)
                oldExchange.getIn().setBody(newResponseModel);
        }
        setSuccessHeaders(oldExchange);
        return oldExchange;
    }

    private void setSuccessHeaders(Exchange exchange) {
        exchange.getIn().removeHeaders("*");
        exchange.getIn().setHeader("CamelHttpResponseCode", HttpStatus.OK.value());
        exchange.getIn().setHeader("CamelHttpResponseText", HttpStatus.OK.getReasonPhrase());
    }
}