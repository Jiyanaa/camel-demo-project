package com.example.spring.cloud.consule.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.spring.cloud.consule.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

	Optional<Book> findByTitle(String title);
}
