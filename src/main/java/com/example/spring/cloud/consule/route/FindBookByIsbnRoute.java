package com.example.spring.cloud.consule.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.PredicateBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.spring.cloud.consule.service.IBookService;

@Component
public class FindBookByIsbnRoute extends ExceptionRoute {

    @Autowired
    private IBookService bookService;

    @Override
    public void configure() throws Exception {
    	
    	globalExceptionHandler();

        from("direct:findBookByIsbn")
                .routeId("findBookByIsbnRouteId")
                .setProperty("isbn").simple("${header.isbn}")
                .choice()
                    .when(PredicateBuilder.isNotNull(header("isbn")))
                        .log(LoggingLevel.DEBUG,"Find book details by isbn method called !")
                        .bean(bookService, "findBookByIsbn")
                    .otherwise()
                        .throwException(ValidationException.class, "Isbn value Is null.")
                .endChoice()
         .end();
    }
}