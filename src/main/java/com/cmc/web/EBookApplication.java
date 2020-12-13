package com.cmc.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jRepositories("com.cmc.web.repository")
public class EBookApplication {
    public static void main(String[] args) {
        SpringApplication.run(EBookApplication.class, args);
    }
}
