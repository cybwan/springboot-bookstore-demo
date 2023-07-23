package io.flomesh.demo.controller;

import io.flomesh.demo.client.BookWarehouseClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class BookThiefController extends BookController {

    @Autowired
    public BookThiefController(BookWarehouseClient client) {
        this.client = client;
    }

    @Override
    protected String getCounterName() {
        return "booksStolen";
    }

    @GetMapping("/books-bought")
    @Override
    public ResponseEntity<Integer> counting() {
        return super.counting();
    }
}
