package io.flomesh.demo.controller;

import io.flomesh.demo.Instants;
import io.flomesh.demo.client.BookWarehouseClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@ConditionalOnProperty(name = "spring.main.web-application-type", havingValue = "SERVLET", matchIfMissing = true)
public class BookstoreController extends BookController {

    @Autowired
    public BookstoreController(BookWarehouseClient client) {
        this.client = client;
    }

    @Override
    protected String getCounterName() {
        return "booksBought";
    }

    @GetMapping("/books-bought")
    @Override
    public ResponseEntity<Integer> counting() {
        return super.counting();
    }

    @GetMapping("/buy-a-book/new")
    public ResponseEntity sellBook() {
        log.info("sellBook() is called");
        if (client.restockBooks(Instants.DEFAULT_ID, 1)) {
            increaseCounter();
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.ok(false);
        }
    }
}
