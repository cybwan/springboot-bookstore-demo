package io.flomesh.demo.service;

import io.flomesh.demo.model.Book;
import io.flomesh.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void restockBooks(String id, int quantity) {
        bookRepository.restock(id, quantity);
    }

    public List<Book> getBooks() {
        return bookRepository.getBooks();
    }
}
