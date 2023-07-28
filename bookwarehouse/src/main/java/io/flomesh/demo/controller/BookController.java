package io.flomesh.demo.controller;

import io.flomesh.demo.model.Book;
import io.flomesh.demo.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@Slf4j
public class BookController {
    private BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/restockBooks/{id}/{quantity}")
    public ResponseEntity<Boolean> restockBooks(@PathVariable String id, @PathVariable int quantity) {
        log.info("restockBooks() is called with id: " + id + ", quantity: " + quantity);
        bookService.restockBooks(id, quantity);
        return ResponseEntity.ok().body(true);
    }

    @GetMapping("/listBooks")
    public ResponseEntity<List<Book>>  listBooks() {
        log.info("listBooks() is called");
        List<Book> books = bookService.getBooks();
        log.info("listBooks() returns: " + books);
        return ResponseEntity.ok().body(books);
    }
}
