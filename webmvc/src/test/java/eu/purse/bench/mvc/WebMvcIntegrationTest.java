package eu.purse.bench.mvc;

import eu.purse.bench.test.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Testcontainers
class WebMvcIntegrationTest extends IntegrationTest {

    ClassPathResource sqlScript = new ClassPathResource("test-data.sql");

    @Autowired
    private DataSource datasource;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        Connection connection = DataSourceUtils.getConnection(datasource);
        ScriptUtils.executeSqlScript(connection, sqlScript);
    }
}
