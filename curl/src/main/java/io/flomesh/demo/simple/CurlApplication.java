package io.flomesh.demo.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CurlApplication {
    public static void main(String[] args) {
        SpringApplication.run(CurlApplication.class, args);
    }
}
