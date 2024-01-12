package io.flomesh.demo.grpc;

import io.flomesh.demo.controller.BookThiefController;
import io.flomesh.demo.grpc.stub.BookstoreServiceGrpc;
import io.flomesh.demo.grpc.stub.Empty;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(name = "protocol", havingValue = "grpc")
@ConditionalOnBean(BookThiefController.class)
public class StealBookGrpcTask {

    @GrpcClient("bookstore")
    private BookstoreServiceGrpc.BookstoreServiceBlockingStub client;
    @Autowired
    private BookThiefController controller;

    @Scheduled(fixedRate = 1000)
    public void buyABook(){
        try {
            if (client.sellBook(Empty.newBuilder().build()).getSuccess()) {
                controller.increaseCounter();
                log.info("A book is stolen");
            }
        } catch (Exception e) {
            log.error("Error while stealing a book", e);
            throw new RuntimeException(e);
        }
    }
}