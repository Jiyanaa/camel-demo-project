package com.example.spring.cloud.consule.processors;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.stereotype.Component;

import com.example.spring.cloud.consule.model.SuccessResponseModel;

@Component
public class UpdateBookStrategy implements AggregationStrategy {

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            SuccessResponseModel newResponseModel = newExchange.getIn().getBody(SuccessResponseModel.class);
            List<SuccessResponseModel> responseModelList = new ArrayList<SuccessResponseModel>();
            responseModelList.add(newResponseModel);
            newExchange.getIn().setBody(responseModelList);
            oldExchange = newExchange;
        }
        else {
            SuccessResponseModel newResponseModel = newExchange.getIn().getBody(SuccessResponseModel.class);
            List<SuccessResponseModel> responseModelList = oldExchange.getIn().getBody(List.class);
            responseModelList.add(newResponseModel);
            oldExchange.getIn().setBody(responseModelList);
        }
        return oldExchange;
    }

}