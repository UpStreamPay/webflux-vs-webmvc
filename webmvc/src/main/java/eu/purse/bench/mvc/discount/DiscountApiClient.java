package eu.purse.bench.mvc.discount;

import eu.purse.bench.configuration.MyConfiguration;
import eu.purse.bench.model.discount.DiscountApplication;
import eu.purse.bench.model.discount.DiscountQuote;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class DiscountApiClient {

    final RestClient restClient;

    public DiscountApiClient(RestClient.Builder builder, MyConfiguration configuration) {
        var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnPerRoute(1000)
                .setMaxConnTotal(1000)
                .build();
        var httpClient = HttpClientBuilder.create()
                .useSystemProperties()
                .setConnectionManager(connectionManager)
                .build();
        this.restClient = builder.requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
                .baseUrl(configuration.getDiscountApiUri().toString())
                .build();
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
