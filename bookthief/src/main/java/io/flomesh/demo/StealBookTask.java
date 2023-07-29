package io.flomesh.demo;

import io.flomesh.demo.client.BookstoreClient;
import io.flomesh.demo.controller.BookThiefController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StealBookTask {

    private final BookstoreClient client;
    private final BookThiefController controller;

    @Autowired
    public StealBookTask(BookThiefController controller, BookstoreClient client) {
        this.controller = controller;
        this.client = client;
    }


    @Scheduled(fixedRate = 1000)
    public void stealBook(){
        try {
            if (client.sellBook()) {
                controller.increaseCounter();
                log.info("A book is stolen");
            }
        } catch (Exception e) {
            log.error("Error while stealing a book", e);
            throw new RuntimeException(e);
        }
    }
}