package com.example.spring.cloud.consule.route;

import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.example.spring.cloud.consule.exception.HttpExceptionHandler;
import com.example.spring.cloud.consule.exception.model.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public abstract class ExceptionRoute extends RouteBuilder {

    @Autowired
    private HttpExceptionHandler httpExceptionHandler;

    protected void globalExceptionHandler() {

        /*errorHandler(deadLetterChannel("direct:errorRoute")
                .maximumRedeliveries(3).redeliveryDelay(5000));

        from("direct:errorRoute")
                .log("exception occured -> ${exception.stacktrace}")
                .setBody(constant(body()))
                .to("{{errorRoute}}");*/


//        onException(javax.validation.ConstraintViolationException.class)
      onException(ConstraintViolationException.class)
                .log(LoggingLevel.ERROR, "validation error occurred - ${exception.stacktrace}")
                .handled(true)
                .process(new Processor() {
                             public void process(Exchange exchange) throws Exception {
                                 Throwable cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
                                 ErrorResponse errorResponse = new ErrorResponse(new Date(), cause.getLocalizedMessage() , String.valueOf(HttpStatus.BAD_REQUEST.value()), exchange.getProperty("isbn", String.class));
                                 exchange.getIn().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                                 exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, errorResponse.getCode());
                                 exchange.getIn().setBody(new ObjectMapper().writeValueAsString(errorResponse));
                             }
                })
        .end();

        onException(Exception.class)
                .log(LoggingLevel.ERROR, "Exception occurred - ${exception.stacktrace}")
                .handled(true)
                .bean(httpExceptionHandler, "errorResponse")
        .end();
    }
}