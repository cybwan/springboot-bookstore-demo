package io.flomesh.demo.controller;

import io.flomesh.demo.Instants;
import io.flomesh.demo.client.BookWarehouseClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class BookController {
    protected BookWarehouseClient client;
    @Getter
    private int counter = 0;

    protected abstract String getCounterName();

    @Value("#{systemEnvironment['IDENTITY'] ?: '${spring.application.name}'}")
    private String identity;

    @GetMapping("/reset")
    public ResponseEntity reset() {
        log.info("resetting booksSold to 0");
        counter = 0;
        return ResponseEntity.ok().build();
    }

    public void increaseCounter() {
        counter++;
    }

    public ResponseEntity<Integer> counting() {
        return ResponseEntity.ok(counter);
    }

    @GetMapping("/raw")
    public ResponseEntity<Map<String, Integer>> raw() {
        HashMap<String, Integer> result = new HashMap<>();
        result.put("counter", counter);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/")
    public String page(Model model) {
        model.addAttribute("identity", identity);
        model.addAttribute("counter", counter);
        model.addAttribute("time", Instants.DEFAULT_DATE_FORMAT.format(new Date()));
        return "index";
    }
}
