package eu.purse.bench.configuration;

import jakarta.validation.constraints.NotNull;
import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "my")
public class MyConfiguration {
    @NotNull
    URI discountApiUri;

    public URI getDiscountApiUri() {
        return discountApiUri;
    }

    public void setDiscountApiUri(URI discountApiUri) {
        this.discountApiUri = discountApiUri;
    }
}
