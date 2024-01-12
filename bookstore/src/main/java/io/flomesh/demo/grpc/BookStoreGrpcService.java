package io.flomesh.demo.grpc;

import io.flomesh.demo.Instants;
import io.flomesh.demo.controller.BookstoreController;
import io.flomesh.demo.grpc.stub.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@GrpcService
@ConditionalOnBean(BookstoreController.class)
public class BookStoreGrpcService extends BookstoreServiceGrpc.BookstoreServiceImplBase {

    @GrpcClient("bookwarehouse")
    private BookWarehouseServiceGrpc.BookWarehouseServiceBlockingStub client;

    @Autowired
    private BookstoreController controller;


    @Override
    public void sellBook(Empty request, StreamObserver<SuccessResponse> responseObserver) {
        SuccessResponse resp = this.client.restockBooks(RestockBooksRequest.newBuilder().setId(Instants.DEFAULT_ID).setQuantity(1).build());
        if (resp.getSuccess()) {
            controller.increaseCounter();
            responseObserver.onNext(SuccessResponse.newBuilder().setSuccess(true).build());
        } else {
            responseObserver.onNext(SuccessResponse.newBuilder().setSuccess(false).build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void reset(Empty request, StreamObserver<Empty> responseObserver) {
        controller.reset();
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void raw(Empty request, StreamObserver<RawResponse> responseObserver) {
        responseObserver.onNext(RawResponse.newBuilder().setCounter(controller.getCounter()).build());
        responseObserver.onCompleted();
    }
}
