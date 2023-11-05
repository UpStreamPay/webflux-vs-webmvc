package eu.purse.bench.mvc;

import eu.purse.bench.mvc.discount.DiscountApiClient;
import eu.purse.bench.mvc.discount.DiscountApplication;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.*;

@RestController
public class Endpoint {

    final Repository repository;
    final DiscountApiClient discountApiClient;

    public Endpoint(Repository repository, DiscountApiClient discountApiClient) {
        this.repository = repository;
        this.discountApiClient = discountApiClient;
    }

    @GetMapping("/products/{id}")
    public Product getProductById(@PathVariable Long id) {
        return repository.getProductById(id);
    }

    @GetMapping("/customers/{id}")
    public Customer getCustomerById(@PathVariable Long id) {
        return repository.getCustomerById(id);
    }

    @PostMapping("/orders")
    public PurchaseOrder createOrder(@RequestBody PurchaseOrderApplication application) {
        // load products, customer
        var customer = repository.getCustomerById(application.customerId());
        var discountItems = new ArrayList<DiscountApplication.Item>();
        int totalAmount = 0;
        for (PurchaseOrderApplication.Item item : application.items()) {
            var product = repository.getProductById(item.productId());
            totalAmount += product.unitPrice() * item.quantity();
            discountItems.add(new DiscountApplication.Item(product, item.quantity()));
        }
        var orderId = repository.createOrder(application, totalAmount, 0);
        // call discount api
        var discountAmount = discountApiClient.getDiscountAmount(new DiscountApplication(customer, discountItems));
        if (discountAmount > 0) {
            repository.updateOrderDiscountAmount(orderId, discountAmount);
        }
        return repository.getOrderById(orderId);
    }
}
