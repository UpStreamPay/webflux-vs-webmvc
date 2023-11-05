package eu.purse.bench.model.discount;

import eu.purse.bench.model.Customer;
import eu.purse.bench.model.Product;
import java.util.List;

public record DiscountApplication(Customer customer, List<Item> items) {
    public record Item(Product product, Integer quantity) {}
}
