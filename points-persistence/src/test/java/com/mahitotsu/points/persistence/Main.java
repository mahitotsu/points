package com.mahitotsu.points.persistence;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

@SpringBootApplication
public class Main {

    public static void main(final String... args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgresql() {

        final String imageName = "public.ecr.aws/docker/library/postgres:16.3";
        final PostgreSQLContainer<?> postgresql = new PostgreSQLContainer<>(
                DockerImageName.parse(imageName).asCompatibleSubstituteFor("postgres"));
        postgresql.withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(imageName)));
        return postgresql;
    }
}
