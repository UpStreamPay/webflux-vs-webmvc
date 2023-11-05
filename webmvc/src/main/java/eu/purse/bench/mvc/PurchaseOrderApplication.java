package eu.purse.bench.mvc;

import java.util.List;

public record PurchaseOrderApplication(Long customerId, List<Item> items) {
    public record Item(Integer rank, Long productId, Integer quantity) {}
}
