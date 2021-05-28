package com.example.spring.cloud.consule.exception;

import java.util.Date;

import org.apache.camel.Exchange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.example.spring.cloud.consule.exception.model.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class HttpExceptionHandler {

    public void errorResponse(Exchange exchange) throws JsonProcessingException {
        Throwable cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);

        ErrorResponse errorResponse;

        if (cause instanceof ResourceNotFoundException) {
            ResourceNotFoundException validationEx = (ResourceNotFoundException) cause;
            errorResponse = new ErrorResponse(new Date(), validationEx.getMessage(), String.valueOf(HttpStatus.NOT_FOUND.value()), exchange.getProperty("isbn", String.class));
        } else if (cause instanceof ValidationException){
            ValidationException validationEx = (ValidationException) cause;
            errorResponse = new ErrorResponse(new Date(), validationEx.getMessage(), String.valueOf(HttpStatus.BAD_REQUEST.value()), exchange.getProperty("isbn", String.class));
        }else {
            errorResponse = new ErrorResponse(new Date(), "Internal Server Error" , String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), exchange.getProperty("isbn", String.class));
        }

        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, errorResponse.getCode());
        exchange.getIn().setBody(new ObjectMapper().writeValueAsString(errorResponse));
    }

}
