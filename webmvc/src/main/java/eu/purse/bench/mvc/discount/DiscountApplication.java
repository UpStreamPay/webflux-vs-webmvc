package eu.purse.bench.mvc.discount;

import eu.purse.bench.mvc.Customer;
import eu.purse.bench.mvc.Product;
import java.util.List;

public record DiscountApplication(Customer customer, List<Item> items) {
    public record Item(Product product, Integer quantity) {}
}
