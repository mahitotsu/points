package com.mahitotsu.points;

import org.slf4j.LoggerFactory;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class TestContext {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {

        final String imageName = "public.ecr.aws/docker/library/postgres:16.4";
        final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
                DockerImageName.parse(imageName).asCompatibleSubstituteFor("postgres"));
        postgreSQLContainer.withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(imageName)));
        return postgreSQLContainer;
    }
}
