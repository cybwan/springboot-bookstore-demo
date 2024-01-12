package io.flomesh.demo.dubbo;

import io.flomesh.demo.Instants;
import io.flomesh.demo.api.BookWarehouseService;
import io.flomesh.demo.api.BookstoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(version = "${service.version.bookstore}")
@Slf4j
public class BookstoreServiceProvider implements BookstoreService {

    @DubboReference(version = "${service.version.bookwarehouse}")
    private BookWarehouseService bookWarehouseService;

    @Override
    public boolean sellBook() {
        log.info("sellBook() is called");
        if(bookWarehouseService.restockBooks(Instants.DEFAULT_ID, 1)) {
            return true;
        } else {
            return false;
        }
    }
}
