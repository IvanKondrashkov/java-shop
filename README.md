# java-shop
![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring](https://img.shields.io/badge/Spring%20Framework-6.1+-green?style=flat-square&logo=spring)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4+-green?style=flat-square&logo=springboot)
![Reactive](https://img.shields.io/badge/Reactive-WebFlux-blue?style=flat-square&logo=react)
![Netty](https://img.shields.io/badge/Server-Netty-success?style=flat-square)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14-blue?style=flat-square&logo=postgresql)
![Gradle](https://img.shields.io/badge/Gradle-8.5+-blue?style=flat-square&logo=gradle)
## Описание
Веб-приложение магазина, разработанное на Spring Framework 6.1+ (Spring Boot) с использованием Java 21. Приложение предоставляет функционал для поиска товаров и их покупки, управление корзиной, просмотр ленты заказов.

### Как запустить контейнер
Сборка jar файла:

```
gradlew clean bootJar
```

Запустите локально Docker:

```shell
docker-compose up -d
```

## Технологии

- **Backend**:
    - Java 21
    - Spring Framework 6.1+
    - Spring WebFlux (Reactive)
    - Spring Data R2DBC
    - Netty Server
    - AWS

- **Frontend**:
    - HTML5
    - Thymeleaf
    - Vanilla JavaScript
    - CSS3

- **База данных**:
    - PostgreSQL
    - R2DBC (Reactive Database Connectivity)

- **Сборка**:
    - Gradle

- **Сервер**:
    - Netty (встроенный в Spring Boot)

## Реактивный стек
Приложение построено на реактивной парадигме с использованием:
 - **Spring WebFlux** - для неблокирующей обработки запросов
 - **Project Reactor** - как основа реактивного программирования
 - **R2DBC** - реактивный драйвер для работы с PostgreSQL
 - **Netty** - высокопроизводительный неблокирующий сервер

## Функционал
 - 📦 Витрина товаров - просмотр товаров с пагинацией, поиском и сортировкой
 - 🛒 Корзина покупок - управление товарами в корзине
 - 📋 Заказы - просмотр истории заказов
 - 🔍 Поиск и фильтрация - по названию и описанию товаров
 - ⚡ Реактивные потоки - неблокирующая обработка запросов
 - 🚀 Высокая производительность - благодаря Netty и реактивному стеку