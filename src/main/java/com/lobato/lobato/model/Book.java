package com.lobato.lobato.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

@Document(collection = "books")
public class Book {
    
    @Id
    private String id;

    @NotBlank(message = "Título é obrigatório")
    @Size(min = 3, max = 150, message = "Título deve ter no mínimo 3 caracteres")
    private String title;

    @NotBlank(message = "Autor é obrigatório")
    @Size(min = 3, max = 150, message = "Autor deve ter no mínimo 3 caracteres")
    private String author;

    @NotBlank(message = "Editora é obrigatória")
    @Size(min = 3, max = 150, message = "Editora deve ter no mínimo 3 caracteres")
    private String publisher;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser pelo menos 1")
    private Integer amount;

    @NotNull(message = "Ano é obrigatório")
    @Min(value = 1000, message = "Ano deve ser válido")
    private Integer year;

    // Constructors
    public Book() {}

    public Book(String title, String author, String publisher, Integer amount, Integer year) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.amount = amount;
        this.year = year;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", amount=" + amount +
                ", year=" + year +
                '}';
    }
}
