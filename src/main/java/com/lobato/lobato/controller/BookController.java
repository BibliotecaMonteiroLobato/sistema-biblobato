package com.lobato.lobato.controller;

import com.lobato.lobato.model.Book;
import com.lobato.lobato.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/books")
    public String books(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        List<Book> allBooks = bookService.findAll();
        model.addAttribute("books", allBooks);
        model.addAttribute("book", new Book()); // Para o formulário de cadastro
        return "books";
    }

    @PostMapping("/register-book")
    public String registerBook(@Valid Book book, BindingResult result, RedirectAttributes redirectAttributes) {
        try {
            // Validar campos obrigatórios
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Por favor, preencha todos os campos obrigatórios corretamente.");
                return "redirect:/books";
            }

            // Verificar se já existe um livro com o mesmo título
            if (bookService.existsByTitle(book.getTitle())) {
                redirectAttributes.addFlashAttribute("error", "Já existe um livro com este título. Escolha outro título.");
                return "redirect:/books";
            }

            // Salvar o livro
            bookService.save(book);
            redirectAttributes.addFlashAttribute("success", "Livro '" + book.getTitle() + "' cadastrado com sucesso!");
            return "redirect:/books";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao cadastrar livro: " + e.getMessage());
            return "redirect:/books";
        }
    }

    @GetMapping("/delete-book")
    public String deleteBook(@RequestParam String title, RedirectAttributes redirectAttributes) {
        try {
            // Encontrar todos os livros com este título (caso existam duplicatas)
            List<Book> booksWithSameTitle = bookService.findAllByTitle(title);
            
            if (booksWithSameTitle.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Livro não encontrado.");
            } else if (booksWithSameTitle.size() == 1) {
                // Apenas um livro encontrado, seguro para excluir
                bookService.delete(booksWithSameTitle.get(0));
                redirectAttributes.addFlashAttribute("success", "Livro '" + title + "' excluído com sucesso!");
            } else {
                // Múltiplos livros encontrados com o mesmo título (duplicatas)
                // Excluir todos e informar o usuário
                bookService.deleteAll(booksWithSameTitle);
                redirectAttributes.addFlashAttribute("success", 
                    "Encontrados " + booksWithSameTitle.size() + " livros duplicados com título '" + title + "'. Todos foram excluídos.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao excluir livro: " + e.getMessage());
        }
        return "redirect:/books";
    }

    @PostMapping("/update-book")
    public String updateBook(@Valid Book book, BindingResult result, RedirectAttributes redirectAttributes) {
        try {
            // Validar campos obrigatórios
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Por favor, preencha todos os campos obrigatórios corretamente.");
                return "redirect:/books";
            }

            // Verificar se já existe outro livro com o mesmo título
            if (bookService.existsByTitleAndIdNot(book.getTitle(), book.getId())) {
                redirectAttributes.addFlashAttribute("error", "Já existe outro livro com este título. Escolha outro título.");
                return "redirect:/books";
            }

            // Atualizar o livro
            bookService.save(book);
            redirectAttributes.addFlashAttribute("success", "Livro '" + book.getTitle() + "' atualizado com sucesso!");
            return "redirect:/books";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar livro: " + e.getMessage());
            return "redirect:/books";
        }
    }

    @GetMapping("/api/book/{id}")
    @ResponseBody
    public Book getBookById(@PathVariable String id) {
        Optional<Book> bookOpt = bookService.findById(id);
        if (bookOpt.isPresent()) {
            return bookOpt.get();
        } else {
            throw new RuntimeException("Livro não encontrado");
        }
    }

    // Endpoints para debug/teste
    @GetMapping("/create-sample-book")
    @ResponseBody
    public String createSampleBook() {
        try {
            Book book = new Book();
            book.setTitle("Dom Casmurro");
            book.setAuthor("Machado de Assis");
            book.setPublisher("Editora Ática");
            book.setAmount(5);
            book.setYear(1899);

            Book savedBook = bookService.save(book);
            return "Livro de exemplo criado com sucesso! ID: " + savedBook.getId();
        } catch (Exception e) {
            return "Erro: " + e.getMessage() + " - " + e.getClass().getSimpleName();
        }
    }

    @GetMapping("/list-all-books")
    @ResponseBody
    public String listAllBooks() {
        try {
            List<Book> books = bookService.findAll();
            StringBuilder response = new StringBuilder();
            response.append("Total de livros encontrados no MongoDB: ").append(books.size()).append("<br><br>");

            for (Book book : books) {
                response.append("Título: ").append(book.getTitle()).append("<br>");
                response.append("Autor: ").append(book.getAuthor()).append("<br>");
                response.append("Editora: ").append(book.getPublisher()).append("<br>");
                response.append("Quantidade: ").append(book.getAmount()).append("<br>");
                response.append("Ano: ").append(book.getYear()).append("<br>");
                response.append("ID: ").append(book.getId()).append("<br>");
                response.append("---<br><br>");
            }

            return response.toString();
        } catch (Exception e) {
            return "MongoDB error: " + e.getMessage();
        }
    }
}