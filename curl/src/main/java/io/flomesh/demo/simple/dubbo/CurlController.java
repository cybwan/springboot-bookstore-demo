package io.flomesh.demo.simple.dubbo;

import lombok.extern.slf4j.Slf4j;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import io.flomesh.demo.api.HttpbinService;

@Controller
@Slf4j
@Profile("dubbo")
public class CurlController{

    @GetMapping("/")
    public @ResponseBody String index() {
        // 记录开始时间
        long startTime = System.nanoTime();
        for(int i=0;i<100000;i++){
            httpbinService.hostname();
        }
        // 记录结束时间
        long  endTime = System.nanoTime();
        return "endTime:"+endTime+ " - startTime:"+startTime +"="+(endTime-startTime);
    }

    @DubboReference(version = "${service.version.httpbin}")
    private HttpbinService httpbinService;
}
