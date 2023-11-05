package eu.purse.bench.mvc;

import java.util.List;

public record PurchaseOrder(Long id, Customer customer, List<Item> items, Integer totalAmount, Integer discountAmount) {
    public record Item(Product product, Integer quantity, Integer unitPrice) {}
}
