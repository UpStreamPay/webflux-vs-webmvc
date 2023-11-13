package eu.purse.bench.webflux;

import eu.purse.bench.model.Customer;
import eu.purse.bench.model.Product;
import eu.purse.bench.model.PurchaseOrder;
import eu.purse.bench.model.PurchaseOrderApplication;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class Repository {
    final DatabaseClient client;

    public Repository(DatabaseClient client) {
        this.client = client;
    }

    public Mono<Product> getProductById(Long id) {
        return client.sql("SELECT id, name, unit_price, brand_name FROM product WHERE id = :id")
                .bind("id", id)
                .map((row, metadata) -> new Product(
                        row.get("id", Long.class),
                        row.get("name", String.class),
                        row.get("unit_price", Integer.class),
                        row.get("brand_name", String.class)))
                .one();
    }

    public Mono<Customer> getCustomerById(Long id) {
        return client.sql("SELECT id, first_name, last_name, email, birth_date FROM customer WHERE id = :id")
                .bind("id", id)
                .map((row, metadata) -> new Customer(
                        row.get("id", Long.class),
                        row.get("first_name", String.class),
                        row.get("last_name", String.class),
                        row.get("email", String.class),
                        row.get("birth_date", java.time.LocalDate.class)))
                .one();
    }

    @Transactional
    public Mono<Long> createOrder(PurchaseOrderApplication order, Integer totalAmount, Integer discountAmount) {
        return client.sql(
                        "INSERT INTO purchase_order (customer_id, total_amount, discount_amount) VALUES (:customer_id, :total_amount, :discount_amount) RETURNING id")
                .bind("customer_id", order.customerId())
                .bind("total_amount", totalAmount)
                .bind("discount_amount", discountAmount)
                //                .filter(statement -> statement.returnGeneratedValues("id"))
                .map((row, metadata) -> row.get("id", Long.class))
                .one()
                .flatMap(id -> {
                    AtomicInteger rank = new AtomicInteger(0);
                    return Flux.fromIterable(order.items())
                            .flatMap(item -> client.sql(
                                            """
                    INSERT INTO purchase_order_item (order_id, rank, product_id, quantity, unit_price)
                    SELECT :order_id, :rank, product.id, :quantity, product.unit_price
                    FROM product
                    WHERE product.id = :product_id
                    """)
                                    .bind("order_id", id)
                                    .bind("rank", rank.getAndIncrement())
                                    .bind("quantity", item.quantity())
                                    .bind("product_id", item.productId())
                                    .fetch()
                                    .rowsUpdated())
                            .then(Mono.just(id));
                });
    }

    public Mono<PurchaseOrder> getOrderById(Long id) {
        final AtomicReference<Customer> customer = new AtomicReference<>();
        final AtomicReference<Integer> totalAmount = new AtomicReference<>();
        final AtomicReference<Integer> discountAmount = new AtomicReference<>();

        return client.sql(
                        """
                SELECT
                    customer.id AS customer_id,
                    customer.first_name AS customer_first_name,
                    customer.last_name AS customer_last_name,
                    customer.email AS customer_email,
                    customer.birth_date AS customer_birth_date,
                    purchase_order.total_amount AS total_amount,
                    purchase_order.discount_amount AS discount_amount,
                    purchase_order_item.quantity AS quantity,
                    purchase_order_item.unit_price AS unit_price,
                    product.id as product_id,
                    product.name AS product_name,
                    product.brand_name AS product_brand_name
                FROM purchase_order
                JOIN purchase_order_item ON purchase_order_item.order_id = purchase_order.id
                JOIN product ON product.id = purchase_order_item.product_id
                JOIN customer ON customer.id = purchase_order.customer_id
                WHERE purchase_order.id = :id
                ORDER BY purchase_order_item.rank
                """)
                .bind("id", id)
                .map((row, metadata) -> {
                    if (customer.get() == null) {
                        customer.set(new Customer(
                                row.get("customer_id", Long.class),
                                row.get("customer_first_name", String.class),
                                row.get("customer_last_name", String.class),
                                row.get("customer_email", String.class),
                                row.get("customer_birth_date", LocalDate.class)));
                        totalAmount.set(row.get("total_amount", Integer.class));
                        discountAmount.set(row.get("discount_amount", Integer.class));
                    }
                    return new PurchaseOrder.Item(
                            new Product(
                                    row.get("product_id", Long.class),
                                    row.get("product_name", String.class),
                                    row.get("unit_price", Integer.class),
                                    row.get("product_brand_name", String.class)),
                            row.get("quantity", Integer.class),
                            row.get("unit_price", Integer.class));
                })
                .all()
                .collectList()
                .map(items -> new PurchaseOrder(id, customer.get(), items, totalAmount.get(), discountAmount.get()));
    }

    public Mono<Void> updateOrderDiscountAmount(Long orderId, Integer discountAmount) {
        return client.sql(
                        "UPDATE purchase_order SET total_amount = (total_amount - :discount_amount), discount_amount = :discount_amount WHERE id = :id")
                .bind("discount_amount", discountAmount)
                .bind("id", orderId)
                .fetch()
                .rowsUpdated()
                .then();
    }
}
