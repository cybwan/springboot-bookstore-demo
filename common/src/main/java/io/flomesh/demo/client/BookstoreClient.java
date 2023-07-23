package io.flomesh.demo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "bookstore")
public interface BookstoreClient {

    @GetMapping("/buy-a-book/new")
    boolean sellBook();
}
