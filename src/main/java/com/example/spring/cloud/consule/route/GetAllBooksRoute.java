package com.example.spring.cloud.consule.route;

import org.apache.camel.LoggingLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.spring.cloud.consule.service.IBookService;

@Component
public class GetAllBooksRoute extends ExceptionRoute {

    @Autowired
    private IBookService bookService;

    @Override
    public void configure() throws Exception {
    	
    	globalExceptionHandler();

        from("direct:getAllBooks")
                .routeId("getAllBooksRouteId")
                .log(LoggingLevel.DEBUG,"Get all books details method called !")
                .bean(bookService, "getAllBooks")
                .choice()
                    .when().simple("${body.size()} == 0")
                        .setBody().simple("No Books Found.")
                .endChoice()
         .end();
    }
}