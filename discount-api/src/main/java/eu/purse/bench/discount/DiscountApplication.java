package eu.purse.bench.discount;

import java.util.List;

public record DiscountApplication(Customer customer, List<Item> items) {
    public record Item(Product product, Integer quantity) {}
}
