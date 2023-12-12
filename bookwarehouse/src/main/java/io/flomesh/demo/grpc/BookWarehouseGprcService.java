package io.flomesh.demo.grpc;

import io.flomesh.demo.grpc.stub.*;
import io.flomesh.demo.service.BookService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.stream.Collectors;


@GrpcService
public class BookWarehouseGprcService extends BookWarehouseServiceGrpc.BookWarehouseServiceImplBase {

    private final BookService bookService;

    public BookWarehouseGprcService(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public void restockBooks(RestockBooksRequest request, StreamObserver<SuccessResponse> responseObserver) {
        this.bookService.restockBooks(request.getId(), request.getQuantity());
        SuccessResponse response = SuccessResponse.newBuilder()
                .setSuccess(true)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listBooks(Empty request, StreamObserver<ListBooksResponse> responseObserver) {
        List<Book> books = this.bookService.getBooks().stream().map(book -> Book.newBuilder()
                .setId(book.getId())
                .setValue(book.getValue())
                .build()).collect(Collectors.toList());
        ListBooksResponse response = ListBooksResponse.newBuilder().addAllBooks(books).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
