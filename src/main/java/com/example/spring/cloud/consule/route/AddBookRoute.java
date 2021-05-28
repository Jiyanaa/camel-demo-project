package com.example.spring.cloud.consule.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.spring.cloud.consule.service.IBookService;

@Component
public class AddBookRoute extends ExceptionRoute{

    @Autowired
    private IBookService bookService;

    @Override
    public void configure() throws Exception {
    	
        globalExceptionHandler();

        from("direct:addBook")
                .routeId("addBookRouteId")
                .setProperty("isbn").simple("${body.isbn}")
                .log(LoggingLevel.DEBUG,"Add book details method called!")
                .bean(bookService, "addBook")
                .choice()
                    .when().simple("${body} != null")
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.CREATED.value()))
                .endChoice()
         .end();

    }
}
