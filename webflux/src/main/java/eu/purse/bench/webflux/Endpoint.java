package eu.purse.bench.webflux;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import eu.purse.bench.model.Customer;
import eu.purse.bench.model.Product;
import eu.purse.bench.model.PurchaseOrderApplication;
import eu.purse.bench.model.discount.DiscountApplication;
import eu.purse.bench.webflux.discount.DiscountApiClient;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class Endpoint {

    final Repository repository;
    final DiscountApiClient discountApiClient;

    public Endpoint(Repository repository, DiscountApiClient discountApiClient) {
        this.repository = repository;
        this.discountApiClient = discountApiClient;
    }

    @Bean
    public RouterFunction<ServerResponse> router() {
        return route().GET("/products/{id}", accept(APPLICATION_JSON), this::getProductById)
                .GET("/customers/{id}", accept(APPLICATION_JSON), this::getCustomerById)
                .POST("/orders", accept(APPLICATION_JSON).and(contentType(APPLICATION_JSON)), this::createOrder)
                .build();
    }

    private Mono<ServerResponse> getProductById(ServerRequest request) {
        return ServerResponse.ok()
                .body(repository.getProductById(Long.valueOf(request.pathVariable("id"))), Product.class);
    }

    private Mono<ServerResponse> getCustomerById(ServerRequest request) {
        return ServerResponse.ok()
                .body(repository.getCustomerById(Long.valueOf(request.pathVariable("id"))), Customer.class);
    }

    private Mono<ServerResponse> createOrder(ServerRequest request) {
        return request.bodyToMono(PurchaseOrderApplication.class).flatMap(application -> loadProducts(application)
                .flatMap(list -> repository
                        .createOrder(application, computeTotalAmount(list), 0)
                        .flatMap(orderId -> computeDiscountAmount(application, list)
                                .flatMap(
                                        discountAmount -> repository.updateOrderDiscountAmount(orderId, discountAmount))
                                .thenReturn(orderId)))
                .flatMap(repository::getOrderById)
                .flatMap(order -> ServerResponse.ok().bodyValue(order)));
    }

    private Mono<Integer> computeDiscountAmount(PurchaseOrderApplication application, List<ProductAndQuantity> list) {
        var items = list.stream()
                .map(item -> new DiscountApplication.Item(item.product(), item.quantity()))
                .toList();
        return repository
                .getCustomerById(application.customerId())
                .flatMap(customer -> discountApiClient.getDiscountAmount(new DiscountApplication(customer, items)));
    }

    private static int computeTotalAmount(List<ProductAndQuantity> list) {
        return list.stream()
                .mapToInt(t -> t.product().unitPrice() * t.quantity())
                .sum();
    }

    private Mono<List<ProductAndQuantity>> loadProducts(PurchaseOrderApplication application) {
        return Flux.fromIterable(application.items())
                .flatMap(item -> repository
                        .getProductById(item.productId())
                        .map(product -> new ProductAndQuantity(product, item.quantity())))
                .collectList();
    }
}

record ProductAndQuantity(Product product, Integer quantity) {}
