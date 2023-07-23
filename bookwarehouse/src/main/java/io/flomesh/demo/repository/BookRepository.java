package io.flomesh.demo.repository;

import io.flomesh.demo.model.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BookRepository {

    private List<Book> books = new ArrayList<>();

    public void restock(String id, int quantity) {
        Book book = books.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElse(new Book(id, 0));
        book.setValue(book.getValue() + quantity);
    }

    public void add(Book book) {
        System.out.println(book);
        books.add(book);
    }

    public List<Book> getBooks() {
        return books;
    }
}
