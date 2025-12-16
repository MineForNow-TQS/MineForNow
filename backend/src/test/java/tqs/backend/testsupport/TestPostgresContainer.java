package tqs.backend.testsupport;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.InputStream;
import java.util.Properties;

public class TestPostgresContainer {

    private static final DockerImageName POSTGRES_IMAGE = DockerImageName.parse("postgres:15.3");

    public static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>(POSTGRES_IMAGE)
                    .withDatabaseName("test_db")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        String externalUrl = System.getenv("TEST_POSTGRES_JDBC_URL");
        String externalUser = System.getenv("TEST_POSTGRES_USERNAME");
        String externalPass = System.getenv("TEST_POSTGRES_PASSWORD");

        if (externalUrl != null && !externalUrl.isBlank()) {
            registry.add("spring.datasource.url", () -> externalUrl);
            registry.add("spring.datasource.username", () -> externalUser != null ? externalUser : "test");
            registry.add("spring.datasource.password", () -> externalPass != null ? externalPass : "test");
            registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
            registry.add("spring.flyway.enabled", () -> "true");
            boolean hasObjects = false;
            try {
                Class.forName("org.postgresql.Driver");
                try (java.sql.Connection c = java.sql.DriverManager.getConnection(externalUrl, externalUser != null ? externalUser : "test", externalPass != null ? externalPass : "test");
                     java.sql.PreparedStatement ps = c.prepareStatement("SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public'");
                     java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        hasObjects = rs.getInt(1) > 0;
                    }
                }
            } catch (Exception e) {
                hasObjects = true;
            }

            if (hasObjects) {
                registry.add("spring.flyway.baseline-on-migrate", () -> "true");
            }
            registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
            registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
            registry.add("spring.test.database.replace", () -> "NONE");
            return;
        }

        try {
            if (!POSTGRES_CONTAINER.isRunning()) {
                POSTGRES_CONTAINER.start();
            }
        } catch (Exception e) {
            System.err.println("[WARN] Testcontainers failed to start: " + e.getMessage());
            try (InputStream is = TestPostgresContainer.class.getResourceAsStream("/application.properties")) {
                if (is != null) {
                    Properties props = new Properties();
                    props.load(is);
                    String url = props.getProperty("spring.datasource.url");
                    String user = props.getProperty("spring.datasource.username");
                    String pass = props.getProperty("spring.datasource.password");
                    if (url != null && !url.isBlank()) {
                        registry.add("spring.datasource.url", () -> url);
                        registry.add("spring.datasource.username", () -> user != null ? user : "test");
                        registry.add("spring.datasource.password", () -> pass != null ? pass : "test");
                        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
                        registry.add("spring.flyway.enabled", () -> "true");
                        registry.add("spring.flyway.baseline-on-migrate", () -> "true");
                        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
                        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
                        registry.add("spring.test.database.replace", () -> "NONE");
                        return;
                    }
                }
            } catch (Exception ex) {
            }

            throw new IllegalStateException("Testcontainers failed to start. Ensure Docker is running and compatible with Testcontainers. "
                    + "Or set TEST_POSTGRES_JDBC_URL/TEST_POSTGRES_USERNAME/TEST_POSTGRES_PASSWORD to use an external Postgres.", e);
        }

        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRES_CONTAINER::getDriverClassName);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.baseline-on-migrate", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.test.database.replace", () -> "NONE");
    }
}
