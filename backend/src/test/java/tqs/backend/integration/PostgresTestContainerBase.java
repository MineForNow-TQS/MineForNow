package tqs.backend.integration;

import tqs.backend.testsupport.AbstractPostgresTest;

/**
 * Base class para integration tests que precisam de Postgres.
 * Reusa o container e as propriedades definidas em testsupport.
 */
public abstract class PostgresTestContainerBase extends AbstractPostgresTest {
}
