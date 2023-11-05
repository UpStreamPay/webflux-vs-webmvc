package eu.purse.bench.mvc;

import static java.util.Map.of;

import eu.purse.bench.model.Customer;
import eu.purse.bench.model.Product;
import eu.purse.bench.model.PurchaseOrder;
import eu.purse.bench.model.PurchaseOrderApplication;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class Repository {
    final NamedParameterJdbcOperations namedJdbc;
    final JdbcOperations jdbc;

    public Repository(NamedParameterJdbcOperations namedJdbc, JdbcOperations jdbc) {
        this.namedJdbc = namedJdbc;
        this.jdbc = jdbc;
    }

    public Product getProductById(Long id) {
        return namedJdbc.queryForObject(
                "SELECT id, name, unit_price, brand_name FROM product WHERE id = :id",
                of("id", id),
                (rs, rowNum) -> new Product(
                        rs.getLong("id"), rs.getString("name"), rs.getInt("unit_price"), rs.getString("brand_name")));
    }

    public Customer getCustomerById(Long id) {
        return namedJdbc.queryForObject(
                "SELECT id, first_name, last_name, email, birth_date FROM customer WHERE id = :id",
                of("id", id),
                (rs, rowNum) -> new Customer(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getDate("birth_date").toLocalDate()));
    }

    @Transactional
    public Long createOrder(PurchaseOrderApplication order, Integer totalAmount, Integer discountAmount) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(
                connection -> {
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "INSERT INTO purchase_order (customer_id, total_amount, discount_amount) VALUES (?, ?, ?) RETURNING id",
                            Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setLong(1, order.customerId());
                    preparedStatement.setInt(2, totalAmount);
                    preparedStatement.setInt(3, discountAmount);
                    return preparedStatement;
                },
                keyHolder);

        Long orderId = keyHolder.getKey().longValue();

        AtomicInteger rank = new AtomicInteger(0);
        order.items()
                .forEach(item -> jdbc.update(
                        """
                    INSERT INTO purchase_order_item (order_id, rank, product_id, quantity, unit_price)
                    SELECT ?, ?, product.id, ?, product.unit_price
                    FROM product
                    WHERE product.id = ?
                    """,
                        orderId,
                        rank.getAndIncrement(),
                        item.quantity(),
                        item.productId()));

        return orderId;
    }

    public PurchaseOrder getOrderById(Long id) {
        return namedJdbc.query(
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
                """,
                of("id", id),
                rs -> {
                    Customer customer = null;
                    Integer totalAmount = null;
                    Integer discountAmount = null;
                    var items = new ArrayList<PurchaseOrder.Item>();
                    while (rs.next()) {
                        if (customer == null) {
                            customer = new Customer(
                                    rs.getLong("customer_id"),
                                    rs.getString("customer_first_name"),
                                    rs.getString("customer_last_name"),
                                    rs.getString("customer_email"),
                                    rs.getDate("customer_birth_date").toLocalDate());

                            totalAmount = rs.getInt("total_amount");
                            discountAmount = rs.getInt("discount_amount");
                        }
                        var product = new Product(
                                rs.getLong("product_id"),
                                rs.getString("product_name"),
                                rs.getInt("unit_price"),
                                rs.getString("product_brand_name"));
                        items.add(new PurchaseOrder.Item(product, rs.getInt("quantity"), rs.getInt("unit_price")));
                    }
                    return new PurchaseOrder(id, customer, items, totalAmount, discountAmount);
                });
    }

    public void updateOrderDiscountAmount(Long orderId, Integer discountAmount) {
        namedJdbc.update(
                "UPDATE purchase_order SET total_amount = (total_amount - :discount_amount), discount_amount = :discount_amount WHERE id = :id",
                Map.of("discount_amount", discountAmount, "id", orderId));
    }
}
