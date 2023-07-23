package io.flomesh.demo;

import io.flomesh.demo.client.BookstoreClient;
import io.flomesh.demo.controller.BookBuyerController;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BuyBookTask {

    private final BookstoreClient client;
    private final BookBuyerController controller;

    @Autowired
    public BuyBookTask(BookBuyerController controller, BookstoreClient client) {
        this.controller = controller;
        this.client = client;
    }


    @Scheduled(fixedRate = 1000)
    public void buyABook(){
        try {
            if (client.sellBook()) {
                controller.getCounter().incrementAndGet();
                log.info("A book is bought");
            }
        } catch (Exception e) {
            log.error("Error while buying a book", e);
            throw new RuntimeException(e);
        }
    }
}