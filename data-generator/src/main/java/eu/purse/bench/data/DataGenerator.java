package eu.purse.bench.data;

import com.github.javafaker.Faker;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.stream.IntStream;

public class DataGenerator {

    public static void main(String[] args) throws IOException {
        Faker faker = new Faker();

        products(faker);
        customers(faker);
    }

    private static void products(Faker faker) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter("products.csv"))) {
            writer.writeNext(new String[] {"id", "name", "unit_price", "brand_name"}, false);
            IntStream.range(0, 5_000_000)
                    .mapToObj(i -> new String[] {
                        String.valueOf(i),
                        faker.commerce().productName(),
                        String.valueOf(faker.number().numberBetween(1, 100000)),
                        faker.company().name()
                    })
                    .forEach(nextLine -> writer.writeNext(nextLine, false));
        }
    }

    private static void customers(Faker faker) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try (CSVWriter writer = new CSVWriter(new FileWriter("customers.csv"))) {
            writer.writeNext(
                    new String[] {
                        "id", "first_name", "last_name", "email", "birth_date",
                    },
                    false);
            IntStream.range(0, 10_000_000)
                    .mapToObj(i -> new String[] {
                        String.valueOf(i),
                        faker.name().firstName(),
                        faker.name().lastName(),
                        faker.internet().emailAddress(),
                        sdf.format(faker.date().birthday())
                    })
                    .forEach(nextLine -> writer.writeNext(nextLine, false));
        }
    }
}
