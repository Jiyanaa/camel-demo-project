package com.example.spring.cloud.consule.service;

import java.util.List;
import java.util.Optional;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.spring.cloud.consule.exception.ResourceNotFoundException;
import com.example.spring.cloud.consule.exception.ValidationException;
import com.example.spring.cloud.consule.exception.model.ErrorResponse;
import com.example.spring.cloud.consule.model.Book;
import com.example.spring.cloud.consule.model.SuccessResponseModel;
import com.example.spring.cloud.consule.repository.BookRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class BookServiceImp implements IBookService {

    @Autowired
    private BookRepository bookRepository;

    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        List<Book> bookList = bookRepository.findAll();
        return bookList;
    }

    @Transactional(readOnly = true)
    public Book findBookByIsbn(@Header("isbn") String isbn) {
        Book bookDetails = validateBookIsPresentOrNotByIsbn(isbn);
        return bookDetails;
    }

    @Transactional
    public SuccessResponseModel updateBookByIsbn(@Body Book book , @Header("isbn") String isbn) {
        Book bookDetails = validateBookIsPresentOrNotByIsbn(isbn);
        bookDetails.setTitle((book.getTitle() != null) ? book.getTitle() : bookDetails.getTitle());
        bookDetails.setAuthor((book.getAuthor() != null) ? book.getAuthor() : bookDetails.getAuthor());
        bookDetails.setPublisher((book.getPublisher() != null) ? book.getPublisher() : bookDetails.getPublisher());
        bookDetails.setYear((book.getYear() != null) ? book.getYear() : bookDetails.getYear());
        bookRepository.save(bookDetails);
        return new SuccessResponseModel("Book Updated.", isbn);
    }

    @Transactional
    public SuccessResponseModel addBook(@Body Book book) {
        validateBookDetails(book);
        validateUniqueByTitle(book.getTitle());
        bookRepository.save(book);
        return new SuccessResponseModel("Book Added.", book.getIsbn());
    }

    @Transactional
    public void deleteBookByIsbn(Exchange exchange) throws JsonProcessingException {
        String isbn = exchange.getProperty("isbn", String.class);
        Book book = validateBookIsPresentOrNotByIsbn(isbn);
        bookRepository.delete(book);
        SuccessResponseModel responseModel = new SuccessResponseModel("Book Deleted", isbn);
        exchange.getIn().setBody(responseModel);
    }

//    @Transactional
    public SuccessResponseModel addBookCollection(Exchange exchange ) {
        Book book = exchange.getIn().getBody(Book.class);
        validateBookDetails(book);
        if(book.getIsbn()!= null)
        	validateUniqueByIsbn(book.getIsbn());
        validateUniqueByTitle(book.getTitle());
        Book bookResponse = bookRepository.save(book);
        SuccessResponseModel responseModel = new SuccessResponseModel("book details updated.", bookResponse.getIsbn());
        return responseModel;
    }

    public void addErrorResponse(Exchange exchange) throws JsonProcessingException {
        ErrorResponse errorResponse = exchange.getIn().getBody(ErrorResponse.class);
		/*
		 * errorRepository.save(errorResponse); exchange.getIn().setBody(new
		 * ObjectMapper().writeValueAsString(errorResponse));
		 */
    }


    ////////////////////////VALIDATION////////////////////////

    private void validateBookDetails(Book book) {
        if(book != null) {
//        	if(book.getIsbn() == null || book.getIsbn().isEmpty())
//                throw new ValidationException("isbn cannot be empty");
            if(book.getTitle() == null || book.getTitle().isEmpty())
                throw new ValidationException("title cannot be empty");
            if(book.getAuthor() == null || book.getAuthor().isEmpty())
                throw new ValidationException("author cannot be empty");
        }
        else{
            throw new ValidationException("book details null");
        }
    }

    private void validateUniqueByIsbn(String isbn) {
        Optional<Book> bookDetails =  bookRepository.findById(isbn);
        if(bookDetails.isPresent())
            throw new ValidationException("isbn value already present");
    }
    
    private void validateUniqueByTitle(String title) {
        Optional<Book> bookDetails =  bookRepository.findByTitle(title);
        if(bookDetails.isPresent())
            throw new ValidationException("title value already present :: " + title);
    }

    private Book validateBookIsPresentOrNotByIsbn(String isbn) {
        Optional<Book> bookDetails = bookRepository.findById(isbn);
        if(bookDetails.isPresent())
            return bookDetails.get();
        else
            throw new ResourceNotFoundException("Book Not Found For isbn :: " + isbn);
    }
}
