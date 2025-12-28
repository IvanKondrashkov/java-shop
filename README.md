# java-shop
![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-green?style=flat-square&logo=openapi-initiative)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4+-green?style=flat-square&logo=springboot)
![Reactive](https://img.shields.io/badge/Reactive-WebFlux-blue?style=flat-square&logo=react)
![Netty](https://img.shields.io/badge/Server-Netty-success?style=flat-square)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14-blue?style=flat-square&logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-7+-red?style=flat-square&logo=redis)
![Keycloak](https://img.shields.io/badge/Keycloak-21.0.0-blue?style=flat-square&logo=keycloak)
## Описание
Веб-приложение магазина, разработанное на Spring Framework 6.1+ (Spring Boot) с использованием Java 21. Приложение предоставляет функционал для поиска товаров и их покупки, управление корзиной, просмотр ленты заказов.


### Архитектура
Система состоит из двух микросервисов:
- **shop-api** - основной сервис магазина (витрина, корзина, заказы)
- **payments-api** - сервис обработки платежей
- **keycloak** - сервис аутентификации и авторизации

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
    - Spring Data Redis Reactive
    - Keycloak
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

- **Кеширование**:
  - Redis (реактивный клиент)
  - Spring Data Redis

- **Аутентификация**:
  - Keycloak (OpenID Connect Identity Provider)
  - JWT
  - OAuth2

- **Документация**:
  - OpenAPI 3.0
  - SpringDoc

- **Сборка**:
    - Gradle

- **Сервер**:
    - Netty (встроенный в Spring Boot)

## Реактивный стек
Приложение построено на реактивной парадигме с использованием:
 - **Spring WebFlux** - для неблокирующей обработки запросов
 - **Project Reactor** - как основа реактивного программирования
 - **R2DBC** - реактивный драйвер для работы с PostgreSQL
 - **Redis Reactive** - реактивный клиент для кеширования
 - **Netty** - высокопроизводительный неблокирующий сервер
 - **Keycloak** - централизованная аутентификация через реактивные OAuth2 клиенты

## API Документация
После запуска приложений документация доступна по адресам:
- **shop-api**: http://localhost:8080/swagger-ui.html
- **payments-api**: http://localhost:8081/swagger-ui.html
- **keycloak**: http://localhost:8082

## Функционал
 - **Витрина товаров** - просмотр товаров с пагинацией, поиском и сортировкой
 - **Корзина покупок** - управление товарами в корзине
 - **Заказы** - просмотр истории заказов
 - **Поиск и фильтрация** - по названию и описанию товаров
 - **Реактивные потоки** - неблокирующая обработка запросов
 - **Высокая производительность** - благодаря Netty и реактивному стеку
 - **Кеширование данных** - благодаря Redis
 - **Документация API** - автоматическая генерация кода на основе документации Open API
 - **Централизованная аутентификация** - единая точка входа через Keycloak
 - **Безопасное межсервисное взаимодействие** - с использованием OAuth2 client credentials flow
 - **Управление ролями и правами** - через административную панель Keycloak