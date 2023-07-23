package io.flomesh.demo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "bookwarehouse")
public interface BookWarehouseClient {

    @PostMapping("/restockBooks/{id}/{quantity}")
    boolean restockBooks(@PathVariable String id, @PathVariable int quantity);
}
