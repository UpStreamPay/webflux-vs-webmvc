package eu.purse.bench.mvc.discount;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class DiscountApiClient {

    final RestClient restClient;

    public DiscountApiClient(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("http://localhost:8081").build();
    }

    public Integer getDiscountAmount(DiscountApplication discountApplication) {
        return restClient
                .post()
                .uri("/quotes")
                .body(discountApplication)
                .retrieve()
                .body(DiscountQuote.class)
                .discountAmount();
    }
}
