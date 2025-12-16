package tqs.backend;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class TestPostgresContainer {

    private static final DockerImageName POSTGRES_IMAGE = DockerImageName.parse("postgres:15.3");

    public static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>(POSTGRES_IMAGE)
                    .withDatabaseName("test_db")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        // Allow using an external Postgres via environment variables when Docker/Testcontainers
        // is not available (useful for developer machines or CI without Docker).
        String externalUrl = System.getenv("TEST_POSTGRES_JDBC_URL");
        String externalUser = System.getenv("TEST_POSTGRES_USERNAME");
        String externalPass = System.getenv("TEST_POSTGRES_PASSWORD");

        if (externalUrl != null && !externalUrl.isBlank()) {
            registry.add("spring.datasource.url", () -> externalUrl);
            registry.add("spring.datasource.username", () -> externalUser != null ? externalUser : "test");
            registry.add("spring.datasource.password", () -> externalPass != null ? externalPass : "test");
            registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
            registry.add("spring.flyway.enabled", () -> "true");
            // If the external DB already contains objects, enable Flyway baseline-on-migrate so
            // Flyway doesn't fail when no schema history table exists. If the DB is empty, do
            // not baseline so migrations will be applied normally.
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
                // If we can't connect or query, fall back to baseline-on-migrate to avoid Flyway errors
                hasObjects = true;
            }

            if (hasObjects) {
                registry.add("spring.flyway.baseline-on-migrate", () -> "true");
            }
            // Avoid Hibernate schema validation interference during tests; let Flyway manage schema
            registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
            // Ensure Hibernate uses Postgres dialect when running against Postgres
            registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
            registry.add("spring.test.database.replace", () -> "NONE");
            return;
        }

        try {
            // start container once for test JVM
            if (!POSTGRES_CONTAINER.isRunning()) {
                POSTGRES_CONTAINER.start();
            }
        } catch (Exception e) {
            // If Testcontainers can't start (Docker not available or incompatible), try to
            // fall back to properties defined in `application.properties` on the test
            // classpath. This makes running `mvn test` possible without Docker by
            // pointing tests at a developer's local Postgres instance defined in
            // `src/main/resources/application.properties`.
            try (java.io.InputStream in = TestPostgresContainer.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (in != null) {
                    java.util.Properties props = new java.util.Properties();
                    props.load(in);
                    String url = props.getProperty("spring.datasource.url");
                    String user = props.getProperty("spring.datasource.username");
                    String pass = props.getProperty("spring.datasource.password");
                    if (url != null && !url.isBlank()) {
                        registry.add("spring.datasource.url", () -> url);
                        registry.add("spring.datasource.username", () -> user != null ? user : "test");
                        registry.add("spring.datasource.password", () -> pass != null ? pass : "test");
                        registry.add("spring.datasource.driver-class-name", () -> props.getProperty("spring.datasource.driver-class-name", "org.postgresql.Driver"));
                        registry.add("spring.flyway.enabled", () -> props.getProperty("spring.flyway.enabled", "true"));
                        registry.add("spring.flyway.baseline-on-migrate", () -> props.getProperty("spring.flyway.baseline-on-migrate", "true"));
                        registry.add("spring.jpa.hibernate.ddl-auto", () -> props.getProperty("spring.jpa.hibernate.ddl-auto", "none"));
                        registry.add("spring.jpa.database-platform", () -> props.getProperty("spring.jpa.database-platform", "org.hibernate.dialect.PostgreSQLDialect"));
                        registry.add("spring.test.database.replace", () -> "NONE");
                        return;
                    }
                }
            } catch (Exception ex) {
                // fall through to rethrow the original Testcontainers exception below
            }

            throw new IllegalStateException("Testcontainers failed to start. Ensure Docker is running and compatible with Testcontainers. "
                    + "Or set TEST_POSTGRES_JDBC_URL/TEST_POSTGRES_USERNAME/TEST_POSTGRES_PASSWORD to use an external Postgres.", e);
        }

        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRES_CONTAINER::getDriverClassName);
        // Ensure Flyway runs against the started container
        registry.add("spring.flyway.enabled", () -> "true");
        // For consistency when running against a started container, baseline on migrate is safe.
        registry.add("spring.flyway.baseline-on-migrate", () -> "true");
        // Avoid Hibernate schema validation interference during tests; let Flyway manage schema
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        // Ensure Hibernate uses Postgres dialect when running against Postgres
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        // Prevent Spring Test from trying to replace the DataSource with an embedded one
        registry.add("spring.test.database.replace", () -> "NONE");
    }
}
