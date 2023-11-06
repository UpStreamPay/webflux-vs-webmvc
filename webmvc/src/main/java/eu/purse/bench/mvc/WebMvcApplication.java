package eu.purse.bench.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(eu.purse.bench.configuration.MyConfiguration.class)
public class WebMvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebMvcApplication.class, args);
    }
}
