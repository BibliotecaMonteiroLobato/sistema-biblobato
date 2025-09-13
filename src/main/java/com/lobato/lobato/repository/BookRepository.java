package com.lobato.lobato.repository;

import com.lobato.lobato.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {
    
    Optional<Book> findByTitle(String title);
    
    List<Book> findAllByTitle(String title);
    
    List<Book> findByAuthor(String author);
    
    List<Book> findByPublisher(String publisher);
    
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    List<Book> findByAuthorContainingIgnoreCase(String author);
    
    boolean existsByTitle(String title);
    
    boolean existsByTitleAndIdNot(String title, String id);
}