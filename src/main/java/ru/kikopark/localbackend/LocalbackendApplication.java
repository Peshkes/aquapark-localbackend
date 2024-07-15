package ru.kikopark.localbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"ru.kikopark.localbackend.modules.authentication.repositories", "ru.kikopark.localbackend.modules.action.repositories", "ru.kikopark.localbackend.modules.base.repositories", "ru.kikopark.localbackend.modules.order.repositories"})
@EntityScan(basePackages = {"ru.kikopark.localbackend.modules.authentication.entities", "ru.kikopark.localbackend.modules.action.entities", "ru.kikopark.localbackend.modules.base.entities", "ru.kikopark.localbackend.modules.order.entities"})
public class LocalbackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocalbackendApplication.class, args);
    }

}
