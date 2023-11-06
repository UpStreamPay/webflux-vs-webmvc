package eu.purse.bench.mvc;

import eu.purse.bench.test.IntegrationTest;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;

class WebMvcIntegrationTest extends IntegrationTest {

    @Test
    @Sql("/test-data.sql")
    void coin() {

    }
}
