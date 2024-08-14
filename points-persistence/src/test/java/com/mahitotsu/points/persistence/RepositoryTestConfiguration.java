package com.mahitotsu.points.persistence;

import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

@SpringBootApplication
public class RepositoryTestConfiguration {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgresql() {

        final String name = "public.ecr.aws/docker/library/postgres:16.4";

        final PostgreSQLContainer<?> psqldb = new PostgreSQLContainer<>(
                DockerImageName.parse(name).asCompatibleSubstituteFor("postgres"));
        psqldb.withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(name)));
        return psqldb;
    }
}
