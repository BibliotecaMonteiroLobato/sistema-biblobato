package com.lobato.lobato.service;

import com.lobato.lobato.model.Book;
import com.lobato.lobato.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Optional<Book> findById(String id) {
        return bookRepository.findById(id);
    }

    public Optional<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    public List<Book> findByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

    public List<Book> findByPublisher(String publisher) {
        return bookRepository.findByPublisher(publisher);
    }

    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Book> searchByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public boolean existsByTitle(String title) {
        return bookRepository.existsByTitle(title);
    }

    public boolean existsByTitleAndIdNot(String title, String id) {
        return bookRepository.existsByTitleAndIdNot(title, id);
    }

    public void delete(Book book) {
        bookRepository.delete(book);
    }

    public void deleteById(String id) {
        bookRepository.deleteById(id);
    }

    public void deleteAll(List<Book> books) {
        bookRepository.deleteAll(books);
    }

    public List<Book> findAllByTitle(String title) {
        return bookRepository.findAllByTitle(title);
    }

    public long count() {
        return bookRepository.count();
    }
}