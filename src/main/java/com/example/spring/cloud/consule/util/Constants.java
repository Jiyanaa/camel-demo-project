package com.example.spring.cloud.consule.util;

public interface Constants {

    //Constant REST Resource Path//
    public static final String BasePath = "/bookstore";
    public static final String GetAllBooksResourcePath = "/books";
    public static final String CreateNewBookResourcePath = "/books";
    public static final String BookByIsbnCommonResourcePath = "/books/{isbn}";

    //direct route constant endpoints//
    public static final String GetAllBooksRoute = "direct:getAllBooks";
    public static final String FindBookByIsbnRoute = "direct:findBookByIsbn";
    public static final String UpdateBookRoute = "direct:updateBookByIsbn";
    public static final String AddBookRoute = "direct:addBook";
    public static final String DeleteBookRoute = "direct:deleteBookByIsbn";
    public static final String AddListOfBooksRoute = "direct:addListOfBooks";
    public static final String AddOrUpdateBooksRoute = "direct:addOrUpdateBook";
    public static final String DeleteBooksFromList = "direct:deleteBooksFromList";

}