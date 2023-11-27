package io.flomesh.demo.simple.controller;

import io.flomesh.demo.simple.feign.HTTPBinClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
public class CurlController {

    private final HTTPBinClient client;

    @Autowired
    public CurlController(HTTPBinClient client) {
        this.client = client;
    }

    @GetMapping("/")
    public @ResponseBody String index() {
        log.info("index() is called");
        return client.index();
    }
}
