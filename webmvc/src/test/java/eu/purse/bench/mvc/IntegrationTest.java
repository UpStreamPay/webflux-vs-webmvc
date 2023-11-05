package eu.purse.bench.mvc;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Testcontainers
@AutoConfigureWebTestClient
class IntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    MockWebServer server = new MockWebServer();

    @BeforeEach
    public void setUp() throws Exception {
        server.start(8081);
    }

    @AfterEach
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:15");

    @Test
    @Sql("/test-data.sql")
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
    @Sql("/test-data.sql")
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
    @Sql("/test-data.sql")
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
