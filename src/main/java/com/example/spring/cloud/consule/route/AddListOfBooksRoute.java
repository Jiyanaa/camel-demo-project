package com.example.spring.cloud.consule.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.PredicateBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.spring.cloud.consule.processors.BookAggregationStrategy;
import com.example.spring.cloud.consule.service.IBookService;


@Component
public class AddListOfBooksRoute extends ExceptionRoute{
    @Autowired
    private IBookService bookService;

    @Autowired
    private BookAggregationStrategy bookAggregationStrategy;

    @Override
    public void configure() throws Exception {
        globalExceptionHandler();

        from("direct:addListOfBooks")
                .routeId("addListOfBookRouteIdID")
                .log(LoggingLevel.DEBUG,"log payload ->  ${body}")
                .split(body()).aggregationStrategy(bookAggregationStrategy)
                    .choice()
                        .when(PredicateBuilder.isNotNull(simple("${body.isbn}")))
                            .setProperty("isbn", simple("${body.isbn}"))
                            .log(LoggingLevel.DEBUG,"add collection of book details method called !")
                            .bean(bookService, "addBookCollection")
                        //added condition to check if title is not present
                        .when(PredicateBuilder.isNull(simple("${body.isbn}")))
                            .log(LoggingLevel.DEBUG,"add collection of book details method called !")
                            .bean(bookService, "addBookCollection")
                        .otherwise()
                            .throwException(ValidationException.class, "Isbn value Is null.")
                    .endChoice()
                .end()
        .end();

    }
}
