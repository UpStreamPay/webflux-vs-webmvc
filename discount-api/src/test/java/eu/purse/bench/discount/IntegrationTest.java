package eu.purse.bench.discount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
class IntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void should_create_quote() {
        webTestClient
                .post()
                .uri("/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        """
                        {
                            "customer": {
                                "id": 2,
                                "first_name": "Jane",
                                "last_name": "Doe",
                                "email": "jane.doe@example.com",
                                "birth_date": "1981-02-02"
                            },
                            "items": [
                                {
                                    "product": {
                                        "id": 1,
                                        "name": "Product 1",
                                        "unit_price": 1000,
                                        "brand_name": "ACME"
                                    },
                                    "quantity": 2
                                },
                                {
                                    "product": {
                                        "id": 2,
                                        "name": "Product 2",
                                        "unit_price": 2000,
                                        "brand_name": "ACME"
                                    },
                                    "quantity": 1
                                }
                            ]
                        }
                        """)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.discount_amount")
                .value(it -> assertThat(it).isBetween(0, 4000), Integer.class);
    }
}
