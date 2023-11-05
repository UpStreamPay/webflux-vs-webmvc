package eu.purse.bench.discount;

import java.util.Random;
import org.springframework.web.bind.annotation.*;

@RestController
public class Endpoint {

    Random random = new Random();

    @PostMapping("/quotes")
    public DiscountQuote quote(@RequestBody DiscountApplication application) throws InterruptedException {
        var totalAmount = application.items().stream()
                .mapToInt(item -> item.product().unitPrice() * item.quantity())
                .sum();
        var discount = random.nextInt(totalAmount);
        Thread.sleep(1000L);
        return new DiscountQuote(discount);
    }
}
