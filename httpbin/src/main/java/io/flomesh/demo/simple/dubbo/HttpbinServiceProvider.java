package io.flomesh.demo.simple.dubbo;

import io.flomesh.demo.api.HttpbinService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;

@DubboService(version = "${service.version.httpbin}")
@Profile("dubbo")
@Slf4j
public class HttpbinServiceProvider implements HttpbinService {

     @Value("${HOSTNAME:localhost}")
    private String hostname;

    public String hostname() {
        return this.hostname;
    }
}
