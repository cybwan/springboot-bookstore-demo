package io.flomesh.demo.dubbo;

import io.flomesh.demo.api.BookstoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BuyBookDubboTask {

    @DubboReference(version = "${service.version.bookstore}")
    private BookstoreService bookstoreService;

    @Scheduled(fixedRate = 1000)
    public void buyABook(){
        try {
            if (bookstoreService.sellBook()) {
                log.info("A book is bought");
            }
        } catch (Exception e) {
            log.error("Error while buying a book", e);
            throw new RuntimeException(e);
        }
    }
}
