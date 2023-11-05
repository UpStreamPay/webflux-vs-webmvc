package eu.purse.bench.loadtest;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.util.Random;

public class LoadSimulation extends Simulation {

    Random random = new Random();
    ChainBuilder browse = exec(session -> session.set("customerId", random.nextInt(10_000_000)))
            .exec(session -> session.set("productId1", random.nextInt(5_000_000)))
            .exec(session -> session.set("productId2", random.nextInt(5_000_000)))
            .exec(http("GET /products/{id}")
                    .get("/products/${productId1}")
                    .check(status().is(200))
                    .check(jsonPath("$.id").ofInt().is(session -> session.get("productId1"))))
            .pause(5)
            .exec(http("GET /products/{id}")
                    .get("/products/${productId2}")
                    .check(status().is(200))
                    .check(jsonPath("$.id").ofInt().is(session -> session.get("productId2"))))
            .pause(5)
            .exec(http("GET /customers/{id}")
                    .get("/customers/${customerId}")
                    .check(status().is(200))
                    .check(jsonPath("$.id").ofInt().is(session -> session.get("customerId"))))
            .pause(5)
            .exec(http("POST /orders")
                    .post("/orders")
                    .header("Content-Type", "application/json")
                    .body(
                            StringBody(
                                    """
                            {
                                "customer_id": ${customerId},
                                "items": [
                                    {
                                        "product_id": ${productId1},
                                        "quantity": 2
                                    },
                                    {
                                        "product_id": ${productId2},
                                        "quantity": 1
                                    }
                                ]
                            }
                            """))
                    .check(status().is(200))
                    .check(jsonPath("$.customer.id").ofInt().is(session -> session.get("customerId")))
                    .check(jsonPath("$.items[0].product.id").ofInt().is(session -> session.get("productId1")))
                    .check(jsonPath("$.items[1].product.id").ofInt().is(session -> session.get("productId2"))));

    HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .acceptEncodingHeader("gzip, deflate");

    ScenarioBuilder users = scenario("Users").exec(browse);

    {
        setUp(users.injectOpen(incrementUsersPerSec(20)
                        .times(5)
                        .eachLevelLasting(20)
                        .separatedByRampsLasting(20)
                        .startingFrom(10))
                .protocols(httpProtocol));
    }
}
