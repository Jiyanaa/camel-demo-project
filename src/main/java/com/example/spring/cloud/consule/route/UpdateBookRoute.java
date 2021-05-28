package com.example.spring.cloud.consule.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.spring.cloud.consule.service.IBookService;

@Component
public class UpdateBookRoute extends ExceptionRoute {

    @Autowired
    private IBookService bookService;

    @Override
    public void configure() throws Exception {
    	
    	globalExceptionHandler();

        Predicate checkHeaderIsNullOrNot = header("isbn").isNotNull();

        from("direct:updateBookByIsbn")
                .routeId("updateBookByIsbnRouteId")
                .setProperty("isbn").simple("${header.isbn}")
                .setHeader("isbn").simple("${header.isbn}")
                .choice()
                    .when(checkHeaderIsNullOrNot)
                        .log(LoggingLevel.DEBUG,"Update book details method called !")
                        .bean(bookService, "updateBookByIsbn")
                    .otherwise()
                        .throwException(ValidationException.class, "Isbn value Is empty")
                .endChoice()
         .end();
    }
}