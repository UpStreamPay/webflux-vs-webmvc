package eu.purse.bench.mvc.discount;

import eu.purse.bench.configuration.MyConfiguration;
import eu.purse.bench.model.discount.DiscountApplication;
import eu.purse.bench.model.discount.DiscountQuote;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class DiscountApiClient {

    final RestClient restClient;

    public DiscountApiClient(RestClient.Builder builder, MyConfiguration configuration) {
        this.restClient = builder.baseUrl(configuration.getDiscountApiUri().toString()).build();
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
