package com.example.spring.cloud.consule.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.example.spring.cloud.consule.model.BookResponse;

@Component
public class DeleteBooksFromList extends ExceptionRoute {


    public void configure() throws Exception {

        globalExceptionHandler();

        from("direct:deleteBooksFromList")
                .routeId("addListOfBooksRouteId")
                .setProperty("transactionId", header("transactionId"))
                .split(body())
                    .setHeader("isbn", simple("${body.isbn}"))
                    .log(LoggingLevel.DEBUG,"Delete book details method called !")
                    .wireTap("direct:deleteBookByIsbn").end()
                    .log(LoggingLevel.INFO, "response after wire type -> ${body}")
                .end()
                .to("direct:setReponseRoute")
                .log(LoggingLevel.INFO,"main route end")
        .end();

        //set asynchronous response//
        from("direct:setReponseRoute")
                .routeId("setReponseRouteId")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        String transactionId = exchange.getProperty("transactionId", String.class);
                        BookResponse response = new BookResponse(transactionId, "Delete Book Call Successful");
                        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.OK.value());
                        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                        exchange.getIn().setBody(response);
                    }
                })
                .log(LoggingLevel.INFO,"final response -> ${body}")
        .end();


    }
}
