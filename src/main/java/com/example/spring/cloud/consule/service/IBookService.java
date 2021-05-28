package com.example.spring.cloud.consule.service;

import java.util.List;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Header;

import com.example.spring.cloud.consule.model.Book;
import com.example.spring.cloud.consule.model.SuccessResponseModel;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface IBookService {
    public List<Book> getAllBooks();
    public Book findBookByIsbn(@Header("isbn") String isbn);
    public SuccessResponseModel updateBookByIsbn(@Body Book book , @Header("isbn") String isbn);
    public SuccessResponseModel addBook(@Body Book book);


    //Implementation using exchange//
    public void deleteBookByIsbn(Exchange exchange) throws JsonProcessingException;
    public SuccessResponseModel addBookCollection(Exchange exchange);
    public void addErrorResponse(Exchange exchange) throws JsonProcessingException;
}