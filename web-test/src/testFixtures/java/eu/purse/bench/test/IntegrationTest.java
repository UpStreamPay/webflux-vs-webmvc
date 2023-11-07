package eu.purse.bench.test;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public abstract class IntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:15");
    MockWebServer server = new MockWebServer();
    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() throws Exception {
        server.start(8888);
    }

    @AfterEach
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    void should_get_product_by_id() {
        webTestClient
                .get()
                .uri("/products/1")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(
                        """
                                {
                                    "id": 1,
                                    "name": "Product 1",
                                    "unit_price": 4299,
                                    "brand_name": "ACME"
                                }
                                """);
    }

    @Test
    void should_get_customer_by_id() {
        webTestClient
                .get()
                .uri("/customers/2")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(
                        """
                                {
                                    "id": 2,
                                    "first_name": "Jane",
                                    "last_name": "Doe",
                                    "email": "jane.doe@example.com",
                                    "birth_date": "1981-02-02"
                                }
                                """);
    }

    @Test
    void should_create_order() {
        server.enqueue(new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody("""
                        {"discount_amount": 98}
                        """));
        webTestClient
                .post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        """
                                {
                                    "customer_id": 2,
                                    "items": [
                                        {
                                            "product_id": 1,
                                            "quantity": 2
                                        },
                                        {
                                            "product_id": 2,
                                            "quantity": 1
                                        }
                                    ]
                                }
                                """)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(
                        """
                                {
                                    "id": 1,
                                    "total_amount": 8599,
                                    "discount_amount": 98,
                                    "customer": {
                                        "id": 2
                                    },
                                    "items": [
                                        {
                                            "product": {"id": 1},
                                            "quantity": 2,
                                            "unit_price": 4299
                                        },
                                        {
                                            "product": {"id": 2},
                                            "quantity": 1,
                                            "unit_price": 99
                                        }
                                    ]
                                }
                                """);
    }
}
