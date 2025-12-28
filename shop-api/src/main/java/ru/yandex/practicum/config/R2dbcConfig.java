package ru.yandex.practicum.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(basePackages = "ru.yandex.practicum.repository")
public class R2dbcConfig {
}