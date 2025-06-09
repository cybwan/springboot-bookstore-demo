package io.flomesh.demo.simple.dubbo;

import lombok.extern.slf4j.Slf4j;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import io.flomesh.demo.api.HttpbinService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@Controller
@Slf4j
@Profile("dubbo")
public class CurlController{

    private static final AtomicLong escapeTime = new AtomicLong(0);

    @GetMapping("/meter")
    public @ResponseBody String meter(@RequestParam int n,@RequestParam int c) throws InterruptedException {
        if(n<1){
            n =1;
        }
        if(c<1){
            c =1;
        }
        int totalRequests =n;
        int concurrencyLevel =c;
        ExecutorService threadPool = Executors.newFixedThreadPool(concurrencyLevel);
        CountDownLatch latch = new CountDownLatch(concurrencyLevel);

        // 每个线程执行 (totalRequests / concurrencyLevel) 次请求
        int requestPerThread = totalRequests / concurrencyLevel;
        totalRequests =requestPerThread*concurrencyLevel;

        //warmup
        for(int i=0;i<5;i++){
            httpbinService.hostname();
        }

        Thread.sleep(5000); // 等待1秒钟，确保warmup完成

        for (int i = 0; i < concurrencyLevel; i++) {
            threadPool.execute(() -> {
                try {
                    // 记录开始时间
                    long startTime = System.nanoTime();
                    for (int j = 0; j < requestPerThread; j++) {
                        httpbinService.hostname();
                    }
                    // 记录结束时间
                    long  endTime = System.nanoTime();
                    escapeTime.addAndGet(endTime - startTime);
                } finally {
                    latch.countDown();
                }
            });
        }

        // 等待所有线程完成
        latch.await();
        threadPool.shutdown();
        return "totalRequests: " + totalRequests +
                ", concurrencyLevel: " + concurrencyLevel +
                ", totalTime: " + escapeTime.get() +
                " ns, avgTime: " + (escapeTime.get() / totalRequests) + " ns";
    }

    @GetMapping("/")
    public @ResponseBody String index(@RequestParam int count) {
        if(count<1){
            count =1;
        }
        // 记录开始时间
        long startTime = System.nanoTime();
        for(int i=0;i<count;i++){
            httpbinService.hostname();
        }
        // 记录结束时间
        long  endTime = System.nanoTime();
        return "endTime:"+endTime+ " - startTime:"+startTime +"="+(endTime-startTime) + " count:"+ count+" avg:"+ ((endTime-startTime)/count)+"ns";
    }

    @GetMapping("/hostname")
    public @ResponseBody String hostname() {
        return httpbinService.hostname();
    }

    @DubboReference(version = "${service.version.httpbin}")
    private HttpbinService httpbinService;
}
