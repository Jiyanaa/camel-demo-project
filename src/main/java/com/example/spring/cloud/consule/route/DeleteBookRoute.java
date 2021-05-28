package com.example.spring.cloud.consule.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.gson.GsonDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.spring.cloud.consule.exception.HttpExceptionHandler;
import com.example.spring.cloud.consule.exception.model.ErrorResponse;
import com.example.spring.cloud.consule.service.IBookService;

@Component
public class DeleteBookRoute extends RouteBuilder {
    @Autowired
    private IBookService bookService;

    @Autowired
    private HttpExceptionHandler httpExceptionHandler;

    @Override
    public void configure() throws Exception {
    	
//    	globalExceptionHandler();

        GsonDataFormat errorResponse = new GsonDataFormat(ErrorResponse.class);

        //log exception and send errorResponse to activemq queue//
        onException(Exception.class)
                .log(LoggingLevel.ERROR, "Exception occurred - ${exception.stacktrace}")
                .handled(true)
                .bean(httpExceptionHandler, "errorResponse")
//                .to("{{errorRoute}}")
        .end();

        from("direct:deleteBookByIsbn")
                .routeId("deleteBookByIsbnRouteId")
//                .setHeader("isbn").simple("${header.isbn}")
                .setProperty("isbn").simple("${header.isbn}")
                .log(LoggingLevel.DEBUG,"Delete book details method called !")
                .bean(bookService, "deleteBookByIsbn")
        .end();

        //add error response from activemq and add it to database//
       /* from("{{errorRoute}}")
                .log(LoggingLevel.INFO, "error message consumed from activemq--> ${body}.")
                .unmarshal(errorResponse)
                .log(LoggingLevel.DEBUG,"Add error details method called!")
                .bean(bookService, "addErrorResponse")
                .log(LoggingLevel.INFO, "error reponse added into database.")
        .end();*/
    }
}
