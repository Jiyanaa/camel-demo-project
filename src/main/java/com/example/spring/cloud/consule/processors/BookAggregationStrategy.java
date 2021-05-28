package com.example.spring.cloud.consule.processors;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.spring.cloud.consule.model.SuccessResponseModel;

@Component
public class BookAggregationStrategy implements AggregationStrategy {

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            SuccessResponseModel newResponseModel = newExchange.getIn().getBody(SuccessResponseModel.class);
            List<SuccessResponseModel> responseModelList = new ArrayList<SuccessResponseModel>();
            setBookResponseForErrorConditions(newResponseModel, newExchange, responseModelList);
            newExchange.getIn().setBody(responseModelList);
            oldExchange = newExchange;
        }
        else {
            SuccessResponseModel newResponseModel = newExchange.getIn().getBody(SuccessResponseModel.class);
            List<SuccessResponseModel> responseModelList = oldExchange.getIn().getBody(List.class);
            setBookResponseForErrorConditions(newResponseModel, newExchange, responseModelList);
            oldExchange.getIn().setBody(responseModelList);
        }
        setSuccessHeaders(oldExchange);
        return oldExchange;
    }
    private void setSuccessHeaders(Exchange exchange) {
        exchange.getIn().removeHeaders("*");
        exchange.getIn().setHeader("CamelHttpResponseCode", HttpStatus.OK.value());
        exchange.getIn().setHeader("CamelHttpResponseText", HttpStatus.OK.getReasonPhrase());
    }

    private void setBookResponseForErrorConditions(SuccessResponseModel responseModel, Exchange newExchange, List<SuccessResponseModel> responseModelList) {
        if(responseModel == null) {
            Throwable cause = newExchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
            String isbn = newExchange.getProperty("isbn", String.class);
            responseModel = new SuccessResponseModel("Opearation Failed -> " + cause.getMessage(), (isbn!=null ? isbn: "Error"));
        }
        responseModelList.add(responseModel);
    }

}