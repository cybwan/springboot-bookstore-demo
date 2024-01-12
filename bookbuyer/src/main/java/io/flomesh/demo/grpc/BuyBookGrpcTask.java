package io.flomesh.demo.grpc;

import io.flomesh.demo.controller.BookBuyerController;
import io.flomesh.demo.grpc.stub.BookstoreServiceGrpc;
import io.flomesh.demo.grpc.stub.Empty;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(name = "protocol", havingValue = "grpc")
@Profile({"eureka", "consul", "nacos"})
public class BuyBookGrpcTask {

    @GrpcClient("bookstore")
    private BookstoreServiceGrpc.BookstoreServiceBlockingStub client;
    @Autowired
    private BookBuyerController controller;

    @Scheduled(fixedRate = 1000)
    public void buyABook(){
        try {
            if (client.sellBook(Empty.newBuilder().build()).getSuccess()) {
                controller.increaseCounter();
                log.info("A book is bought");
            }
        } catch (Exception e) {
            log.error("Error while buying a book", e);
            throw new RuntimeException(e);
        }
    }
}