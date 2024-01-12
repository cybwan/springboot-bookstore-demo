package io.flomesh.demo.api;

public interface  BookWarehouseService {
    boolean restockBooks(String id, int quantity);
}