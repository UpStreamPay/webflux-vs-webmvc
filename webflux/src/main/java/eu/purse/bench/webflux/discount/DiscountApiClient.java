package eu.purse.bench.webflux.discount;

import eu.purse.bench.configuration.MyConfiguration;
import eu.purse.bench.model.discount.DiscountApplication;
import eu.purse.bench.model.discount.DiscountQuote;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class DiscountApiClient {

    final WebClient webClient;

    public DiscountApiClient(WebClient.Builder builder, MyConfiguration configuration) {
        this.webClient = builder.baseUrl(configuration.getDiscountApiUri().toString()).build();
    }

    public Mono<Integer> getDiscountAmount(DiscountApplication discountApplication) {
        return webClient
                .post()
                .uri("/quotes")
                .bodyValue(discountApplication)
                .retrieve()
                .bodyToMono(DiscountQuote.class)
                .map(DiscountQuote::discountAmount);
    }
}
