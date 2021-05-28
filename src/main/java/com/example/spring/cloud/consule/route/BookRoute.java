package com.example.spring.cloud.consule.route;

import static com.example.spring.cloud.consule.util.Constants.AddBookRoute;
import static com.example.spring.cloud.consule.util.Constants.AddListOfBooksRoute;
import static com.example.spring.cloud.consule.util.Constants.AddOrUpdateBooksRoute;
import static com.example.spring.cloud.consule.util.Constants.BasePath;
import static com.example.spring.cloud.consule.util.Constants.BookByIsbnCommonResourcePath;
import static com.example.spring.cloud.consule.util.Constants.CreateNewBookResourcePath;
import static com.example.spring.cloud.consule.util.Constants.DeleteBookRoute;
import static com.example.spring.cloud.consule.util.Constants.DeleteBooksFromList;
import static com.example.spring.cloud.consule.util.Constants.FindBookByIsbnRoute;
import static com.example.spring.cloud.consule.util.Constants.GetAllBooksResourcePath;
import static com.example.spring.cloud.consule.util.Constants.GetAllBooksRoute;
import static com.example.spring.cloud.consule.util.Constants.UpdateBookRoute;

import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.example.spring.cloud.consule.exception.ResourceNotFoundException;
import com.example.spring.cloud.consule.exception.ValidationException;
import com.example.spring.cloud.consule.model.Book;
import com.example.spring.cloud.consule.model.SuccessResponseModel;

@Component
public class BookRoute extends ExceptionRoute {

    @Autowired
    private Environment env;
    
    @Value("${camel.component.servlet.mapping.context-path}")
    private String contextPath;

	

    @Override
    public void configure() throws Exception {

//        globalExceptionHandler();

        restConfiguration()
                .component("servlet")
                .host("localhost").port(env.getProperty("server.port", "8080"))
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .enableCORS(true)
                .contextPath(contextPath.substring(0, contextPath.length() - 2))
                // turn on swagger
                .apiContextPath("/swagger")
                .apiProperty("api.title", "BOOK API")
                .apiProperty("api.version", "1.0.0");
        		; 

        rest().description("Book REST API Using Spring")
                .path(BasePath)
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
//    			.skipBindingOnErrorCode(false) //Enable json marshalling for body in case of errors

         
                .get(GetAllBooksResourcePath)
                    .description("Find all Books")
                    .responseMessage().code(200).message("All Books details successfully returned").responseModel(Book[].class).endResponseMessage()
                    .responseMessage().code(500).message("Server Error").endResponseMessage()
                   	.route().routeId("listallbooks-route")
                     .to(GetAllBooksRoute)
                    .endRest()

                .get(BookByIsbnCommonResourcePath)
                    .description("Find Book By ISBN Number")
                    .param().name("isbn").description("The ISBN of the Book").type(RestParamType.path).endParam()
                    .responseMessage().code(200).message("Book successfully returned").responseModel(Book.class).endResponseMessage()
                    .responseMessage().code(404).message("Book Not Found Exception").responseModel(ResourceNotFoundException.class).endResponseMessage()
                    .responseMessage().code(500).message("Server Error").endResponseMessage()
        			.route().routeId("findbookbyisbn-route")
                     .to(FindBookByIsbnRoute)
                    .endRest()

                .put(BookByIsbnCommonResourcePath)
                    .description("Updates Book Details By ISBN Number")
                    .type(Book.class)
                    .param().name("isbn").description("The ISBN of the Book").type(RestParamType.path).endParam()
                    .responseMessage().code(200).message("Book details updated").responseModel(SuccessResponseModel.class).endResponseMessage()
                    .responseMessage().code(404).message("Book Not Found Exception").responseModel(ResourceNotFoundException.class).endResponseMessage()
                    .responseMessage().code(500).message("Server Error").endResponseMessage()
        			.route().routeId("updatebookbyisbn-route")
                    	.to(UpdateBookRoute)
                    .endRest()

                .post(CreateNewBookResourcePath)
                    .description("Add Book Details")
                    .type(Book.class)
                    .responseMessage().code(201).message("Book Added").responseModel(SuccessResponseModel.class).endResponseMessage()
                    .responseMessage().code(400).message("Validation Exception").responseModel(ValidationException.class).endResponseMessage()
                    .responseMessage().code(500).message("Server Error").endResponseMessage()
        			.route().routeId("createnewbook-route")
                    	.to(AddBookRoute)
                    .endRest()

                .delete(BookByIsbnCommonResourcePath)
                    .description("Delete Book Details By ISBN Number")
                    .param().name("isbn").description("The ISBN of the Book").type(RestParamType.path).endParam()
                    .responseMessage().code(200).message("Book details updated").responseModel(SuccessResponseModel.class).endResponseMessage()
                    .responseMessage().code(404).message("Book Not Found Exception").responseModel(ResourceNotFoundException.class).endResponseMessage()
                    .responseMessage().code(500).message("Server Error").endResponseMessage()
        			.route().routeId("deletebookbyisbn-route")
                    	.to(DeleteBookRoute)
                    .endRest()

                .post("/book/list")
                    .description("Add List Of Book Details")
                    .typeList(String[].class)
                    .responseMessage().code(200).message("Book details updated").responseModel(SuccessResponseModel[].class).endResponseMessage()
                    .responseMessage().code(400).message("Validation Exception").responseModel(ValidationException.class).endResponseMessage()
                    .responseMessage().code(500).message("Server Error").endResponseMessage()
        			.route().routeId("addlistofbooks-route")
        				.to(AddListOfBooksRoute)
        			.endRest()

                .put("/book/list")
                    .description("Add Or Update Book")
                    .typeList(String[].class)
                    .responseMessage().code(200).message("Book details updated").responseModel(SuccessResponseModel[].class).endResponseMessage()
                    .responseMessage().code(400).message("Validation Exception").responseModel(ValidationException.class).endResponseMessage()
                    .responseMessage().code(500).message("Server Error").endResponseMessage()
                    //route
        			.route().routeId("addorupdatelistofbooks-route")
                    	.to(AddOrUpdateBooksRoute)
                    .endRest()

                .delete("/book/list")
                    .description("Delete list of books using wiretype")
                    .typeList(String[].class)
                    .param().name("transactionId").description("The Transaction Id of the Request").type(RestParamType.query).endParam()
                    .responseMessage().code(200).message("Book successfully returned").responseModel(SuccessResponseModel[].class).endResponseMessage()
                    .responseMessage().code(404).message("Book Not Found Exception").responseModel(ResourceNotFoundException.class).endResponseMessage()
                    .responseMessage().code(500).message("Server Error").endResponseMessage()
        			.route().routeId("deletelistofbookbyisbn-route")
                    	.to(DeleteBooksFromList)
                    .endRest()
               ;

    }
}
