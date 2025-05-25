# Большая домашняя работа  №2 по дисциплине конструирование программного обеспечения
# Инструкция по запуску и отчёт по проекту

## Содержание репозитория

```
/project-root
├── eureka-server/                 # Eureka Service Discovery
│   ├── src/                       
│   ├── build.gradle.kts
│   └── Dockerfile
├── api-gateway/                   # Базовый API Gateway (Spring Cloud Gateway)
│   ├── src/                      
│   ├── build.gradle.kts
│   └── Dockerfile
├── my-api-gateway/                # Основной шлюз-прокси с бизнес-логикой
│   ├── src/                       
│   ├── build.gradle.kts
│   └── Dockerfile
├── file-storing-service/          # Сервис хранения файлов
│   ├── src/                       
│   ├── build.gradle.kts
│   └── Dockerfile
├── file-analysis-service/         # Сервис анализа файлов
│   ├── src/                       
│   ├── build.gradle.kts
│   └── Dockerfile
└── docker-compose.yml             # Оркестрация всех контейнеров
```

---

## Технологии

* **Java 17**
* **Spring Boot** 
* **Spring Cloud Netflix Eureka** — сервис регистрации и discovery
* **Spring Cloud Gateway** — API Gateway (WebFlux)
* **Spring WebMVC** блокирующих контроллеров (RestTemplate)
* **Spring Data JPA** + **H2** — для хранения данных во время исполнения
* **SpringDoc OpenAPI** (Swagger UI)
* **Docker** & **Docker Compose**

---

## Архитектура микросервисов

1. **eureka-server** (порт **8761**) — централизованный сервер Discovery.
2. **api-gateway** (порт **8081**) — маршрутизирует внешние запросы.
3. **my-api-gateway** (порт **8082**) — пользовательский шлюз, отвечающий за:

   * Приём запросов по Swagger UI
   * Пересылку на file-storing-service и file-analysis-service
   * Обработку ошибок
4. **file-storing-service** (порт **8083**) — хранит файлы в БД, возвращает `id` и содержимое.
5. **file-analysis-service** (порт **8084**) — запрашивает содержимое по `id`, считает:

   * количество абзацев
   * количество слов
   * количество символов
     Сохраняет результаты в собственной БД и кеширует их.

**Взаимодействие:**

* Клиент → **my-api-gateway**

  * `POST /files/upload` → сохраняет файл в **file-storing-service** → возвращает `id`
  * `GET /files/download/{id}` → получает содержимое из **file-storing-service**
  * `GET /files/analysis/{id}` → проксирует в **file-analysis-service** → возвращает JSON со статистикой

* **file-analysis-service** → сначала проверяет локальную БД → при отсутствии запрашивает файл у storage → сохраняет и отдаёт статистику

---

## Инструкция по запуску

### 1. Предварительные требования

* Установлён **Docker** (Docker Engine) и **Docker Compose**
* Убедитесь, что порты **8761**, **8081**, **8082**, **8083**, **8084** свободны

### 2. Сборка и запуск контейнеров

В корне проекта выполните:

```bash
# Сборка образов и старт сервисов в фоне
docker-compose up --build -d
```

### 3. Проверка сервисов

* **Eureka UI**:
  [http://localhost:8761](http://localhost:8761)
* **My API Gateway (основной)**:
  [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)


### 4. Остановка и очистка

```bash
docker-compose down
```

---
