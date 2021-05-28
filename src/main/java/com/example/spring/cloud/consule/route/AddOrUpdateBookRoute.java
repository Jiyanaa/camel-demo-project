package com.example.spring.cloud.consule.route;

import org.apache.camel.LoggingLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.spring.cloud.consule.processors.ResponseAggregationStrategy;
import com.example.spring.cloud.consule.processors.UpdateBookStrategy;

@Component
public class AddOrUpdateBookRoute extends ExceptionRoute{

    @Autowired
    private UpdateBookStrategy bookAggregationStrategy;

    @Autowired
    private ResponseAggregationStrategy responseAggregationStrategy;

    @Override
    public void configure() throws Exception {
        globalExceptionHandler();

        from("direct:addOrUpdateBook")
                .routeId("addOrUpdateBookRouteId")
                .log(LoggingLevel.DEBUG,"log payload - > ${body}")
                .split(body())
                    .aggregationStrategy(bookAggregationStrategy)
                    .setProperty("isbn").simple("${body.isbn}")
                    .setHeader("isbn", simple("${body.isbn}"))
                    .multicast()
                        .aggregationStrategy(responseAggregationStrategy)
                        .parallelProcessing()
                        .to("direct:addBook")
                        .to("direct:updateBookByIsbn")
//                        .to("direct:updateBookByIsbn")
//                        .to("direct:addBook")
                    .end()
                .end()
                .log(LoggingLevel.INFO,"log after split -> ${body}")
        .end();
    }
}