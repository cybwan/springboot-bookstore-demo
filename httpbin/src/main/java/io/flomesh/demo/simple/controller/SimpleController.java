package io.flomesh.demo.simple.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
@Profile({"eureka", "consul", "nacos"})
public class SimpleController {

    @Value("${HOSTNAME:localhost}")
    private String hostname;

    @GetMapping("/")
    public @ResponseBody String index() {
        log.info("index() is called");
        return hostname;
    }
}