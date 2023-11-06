package com.jromans.hwk;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class BaseIntegrationTest {

    @ServiceConnection
    protected static final PostgreSQLContainer<?> dbContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres")
                    .withTag("16-alpine"))
            .withDatabaseName("itest_database")
            .withUsername("postgres")
            .withPassword("password");

    static {
        dbContainer.start();
    }
}
