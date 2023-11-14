package eu.purse.bench.webflux.discount;

import eu.purse.bench.configuration.MyConfiguration;
import eu.purse.bench.model.discount.DiscountApplication;
import eu.purse.bench.model.discount.DiscountQuote;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Component
public class DiscountApiClient {

    final WebClient webClient;

    public DiscountApiClient(WebClient.Builder builder, MyConfiguration configuration) {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("myConnectionPool")
            .maxConnections(10_000)
        .pendingAcquireMaxCount(1)
        .build();
        ReactorClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(HttpClient.create(connectionProvider));

        this.webClient = builder
            .clientConnector(clientHttpConnector)
            .baseUrl(configuration.getDiscountApiUri().toString()).build();
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
