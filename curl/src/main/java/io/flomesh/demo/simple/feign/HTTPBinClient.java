package io.flomesh.demo.simple.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "http://httpbin") // can NOT use httpbin directly due to starting with http will be operated as schema
public interface HTTPBinClient {

    @GetMapping("/")
    String index();
}
