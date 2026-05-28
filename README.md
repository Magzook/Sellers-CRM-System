# Введение

REST-сервис для управления продавцами и транзакциями. Тестовое задание на стажировку ШИФТ ЛАБ.

Система состоит из сервера на Spring Boot, и базы данных PostgreSQL.

Присутствуют автотесты.

### Функционал
<details>

- Список всех продавцов;
- Инфо о конкретном продавце;
- Создать нового продавца;
- Обновить инфо о продавце;
- Удалить продавца;
- Получить список всех транзакций;
- Получить информацию о конкретной транзакции;
- Создать новую транзакцию;
- Получить все транзакции продавца; 
- Получить самого продуктивного продавца; 
- Получить список продавцов с суммой меньше указанной.
</details>

### Стек Java
<details>

- Версия Java: 17

- SDK: ms-17 (JDK 17)

- Сборщик: Gradle (Groovy DSL)
</details>

### Зависимости
<details>

#### База данных

| Зависимость                    | Описание                                                                                |
|--------------------------------|-----------------------------------------------------------------------------------------|
| `spring-boot-starter-data-jpa` | Интеграция с JPA через Hibernate. Упрощает работу с БД: маппинг сущностей, репозитории. |
| `postgresql`                   | JDBC-драйвер для подключения к PostgreSQL.                                              |

#### Веб

| Зависимость                  | Описание                                                            |
|------------------------------|---------------------------------------------------------------------|
| `spring-boot-starter-webmvc` | Spring MVC для построения REST API: контроллеры, сериализация JSON. |

#### Swagger UI

| Зависимость                           | Описание                                                                                                               |
|---------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| `springdoc-openapi-starter-webmvc-ui` | Автоматически генерирует OpenAPI-спецификацию и предоставляет интерфейс Swagger UI по адресу `/swagger-ui/index.html`. |

#### Валидация

| Зависимость                      | Описание                                                                       |
|----------------------------------|--------------------------------------------------------------------------------|
| `spring-boot-starter-validation` | Позволяет валидировать данные аннотациями: `@NotNull`, `@Size`, `@Email` и др. |

#### Тестирование

| Зависимость                       | Описание                                                                              |
|-----------------------------------|---------------------------------------------------------------------------------------|
| `spring-boot-starter-webmvc-test` | Инструменты для тестирования MVC-слоя (`MockMvc`), утилиты для проверки HTTP-ответов. |
| `junit-platform-launcher`         | Платформа запуска тестов.                                                             |
| `h2`                              | Встраиваемая in-memory база данных. Используется в тестах вместо PostgreSQL.          |
</details>

### Развёртывание

<details>

1) Скачать проект

2) Собрать JAR. Он появится по пути `build/libs`
```text
./gradlew bootJar 
```

3) Собрать и запустить контейнеры Docker.
```text
docker-compose -p sellers-system up --build -d
```
Файлы конфигурации сборки и запуска: compose.yaml, Dockerfile.

Сервер Spring Boot будет работать на порту `8080`.

База данных PostgreSQL будет работать на порту `5432`.

Количество системных ресурсов для сервера и БД явно не установлено.

Swagger UI будет работать по адресу http://localhost:8080/swagger-ui/index.html#.

</details>

# Описание API

Базовый URL для всех запросов: `/api/v1`

Тип контента запросов и ответов: `application/json`

## Шаблонные ответы
<details>

### Ошибка валидации
<details>

**`400 BAD REQUEST`**
Пример:
```json
{
  "message": "Validation failed",
  "timestamp": "2026-05-22T03:09:48.212778",
  "details": [
    "name cannot be empty"
  ]
}
```
</details>

### Ошибка чтения enum в JSON
<details>

**`400 BAD REQUEST`**
Пример:
```json
{
  "message": "Unreadable enum value",
  "timestamp": "2026-05-22T06:01:51.6090707",
  "details": [
    "Accepted values are: [CASH, CARD, TRANSFER]"
  ]
}
```
</details>

### Нечитаемый JSON
<details>

**`400 BAD REQUEST`**
Пример:
```json
{
  "message": "Unreadable JSON request",
  "timestamp": "2026-05-22T06:05:05.5615566",
  "details": [
    "JSON parse error: Unexpected character ('w' (code 119)): was expecting double-quote to start property name"
  ]
}
```
</details>

### Ошибка типа данных аргумента в URL
<details>

**`400 BAD REQUEST`**
Пример:
```json
{
  "message": "Method argument type mismatch",
  "timestamp": "2026-05-26T23:37:00.5873559",
  "details": [
    "id should be a valid int and 123451234512345 isn't"
  ]
}
```
</details>

### Сущность по id не найдена
<details>

**`404 NOT FOUND`**
Пример:
```json
{
  "message": "transaction with id 23324 not found",
  "timestamp": "2026-05-22T06:07:22.1777839",
  "details": []
}
```
</details>

</details>

## Продавцы
<details>

### 1. Список всех продавцов
<details>

```
GET /sellers
```
#### Ответ 1. Стандартный
**`200 OK`**
```json
{
  "sellers": [
    {
      "id": 1073741824,
      "name": "string",
      "contactInfo": "string",
      "registrationDate": "2026-05-21T19:46:47.479Z"
    }
  ]
}
```
| Поле               | Описание                   | Тип                   |
|--------------------|----------------------------|-----------------------|
| `id`               | Id продавца                | int                   |
| `name`             | Имя                        | string                |
| `contactInfo`      | Контактные данные          | string                |
| `registrationDate` | Дата регистрации в системе | string (дата и время) |
</details>

### 2. Инфо о конкретном продавце
<details>

```
GET /sellers/{id}
```
| Параметр | Описание    | Тип |
|----------|-------------|-----|
| `id`     | id продавца | int |

#### Ответ 1. Стандартный
**`200 OK`**
```json
{
  "id": 1073741824,
  "name": "string",
  "contactInfo": "string",
  "registrationDate": "2026-05-21T19:47:47.257Z"
}
```
#### Ответ 2. Продавца с таким id не существует
**`404 NOT FOUND`**
```json
{
  "message": "seller with id 1073741824 not found",
  "timestamp": "2026-05-22T03:09:48.212778",
  "details": []
}
```
</details>

### 3. Создать нового продавца
<details>

```
POST /sellers
```
```json
{
  "name": "string",
  "contactInfo": "string"
}
```
| Поле          | Описание          | Тип    | Ограничения                       |
|---------------|-------------------|--------|-----------------------------------|
| `name`        | Имя               | string | Не пустое, не длиннее 16 символов |
| `contactInfo` | Контактные данные | string | Не пустое                         |

#### Ответ 1. Стандартный
**`200 OK`**
```json
{
  "id": 1073741824,
  "name": "string",
  "contactInfo": "string",
  "registrationDate": "2026-05-21T19:47:47.257Z"
}
```
#### Ответ 2. Имя пустое
**`400 BAD REQUEST`**
```json
{
  "message": "Validation failed",
  "timestamp": "2026-05-22T03:09:48.212778",
  "details": [
    "name cannot be empty"
  ]
}
```
#### Ответ 3. Имя длиннее 16 символов
**`400 BAD REQUEST`**
```json
{
  "message": "Validation failed",
  "timestamp": "2026-05-22T03:09:48.212778",
  "details": [
    "name cannot be longer than 16 characters"
  ]
}
```
#### Ответ 4. Контактные данные пустые
**`400 BAD REQUEST`**
```json
{
  "message": "Validation failed",
  "timestamp": "2026-05-22T03:09:48.212778",
  "details": [
    "contactInfo cannot be empty"
  ]
}
```
</details>

### 4. Изменить продавца
<details>

```
PUT /sellers/{id}
```
Тело запроса и ответы такие же, как в п.3 (плюс случай, когда id не существует)
</details>

### 5. Удалить продавца
<details>

```
DELETE /sellers/{id}
```
#### Ответ 1. Стандартный
**`200 OK`**

<тело отсутствует>
</details>
</details>

## Транзакции
<details>

### 1. Список всех транзакций
<details>

```
GET /transactions
```
#### Ответ 1. Стандартный
**`200 OK`**
```json
{
  "transactions": [
    {
      "id": 1073741824,
      "amount": 0,
      "paymentType": "CASH",
      "transactionDate": "2026-05-21T22:49:41.471Z",
      "sellerId": 1073741824
    }
  ]
}
```
| Параметр/поле     | Описание                           | Тип      |
|-------------------|------------------------------------|----------|
| `id`              | Id транзакции                      | long     |
| `sellerId`        | Id продавца                        | int      |
| `amount`          | Сумма транзакции                   | numeric  |
| `paymentType`     | Тип оплаты                         | string   |
| `transactionDate` | Дата и время совершения транзакции | datetime |
</details>

### 2. Инфо о конкретной транзакции
<details>

```
GET /transactions/{id}
```
| Параметр | Описание      | Тип  |
|----------|---------------|------|
| `id`     | id транзакции | long |

#### Ответ 1. Стандартный
**`200 OK`**
```json
{
  "id": 1073741824,
  "amount": 0,
  "paymentType": "CASH",
  "transactionDate": "2026-05-21T22:50:16.103Z",
  "seller": {
    "id": 1073741824,
    "name": "string",
    "contactInfo": "string",
    "registrationDate": "2026-05-21T22:50:16.103Z"
  }
}
```

#### Ответ 2. Транзакции с таким id не существует
**`404 NOT FOUND`**
```json
{
  "message": "transaction with id 23 not found",
  "timestamp": "2026-05-22T05:51:30.9783893",
  "details": []
}
```
</details>

### 3. Создать новую транзакцию
<details>

```
POST /transactions
```
```json
{
  "amount": 0,
  "paymentType": "CASH",
  "sellerId": 1073741824
}
```
| Поле          | Описание         | Тип     | Ограничения                                                           |
|---------------|------------------|---------|-----------------------------------------------------------------------|
| `amount`      | Сумма транзакции | numeric | Не меньше 0, не больше 999 999 999 999, не более 2 знаков после точки |
| `paymentType` | Тип оплаты       | string  | Только "CASH", "CARD" или "TRANSFER"                                  |

#### Ответ 1. Стандартный
**`200 OK`**
```json
{
  "id": 1073741824,
  "amount": 0,
  "paymentType": "CASH",
  "transactionDate": "2026-05-21T22:48:51.410Z",
  "sellerId": 1073741824
}
```
#### Ответ 2. Сумма меньше 0
**`400 BAD REQUEST`**
```json
{
  "message": "Validation failed",
  "timestamp": "2026-05-22T06:12:59.7588823",
  "details": [
    "amount cannot be negative"
  ]
}
```
#### Ответ 3. Сумма больше 999 999 999 999 или после точки более 2 знаков
**`400 BAD REQUEST`**
```json
{
  "message": "Validation failed",
  "timestamp": "2026-05-22T06:14:11.1200443",
  "details": [
    "amount must fit numeric(14,2)"
  ]
}
```
#### Ответ 4. Тип оплаты некорректный
**`400 BAD REQUEST`**
```json
{
  "message": "Unreadable enum value",
  "timestamp": "2026-05-22T06:15:18.3978919",
  "details": [
    "Accepted values are: [CASH, CARD, TRANSFER]"
  ]
}
```
#### Ответ 5. Продавца с таким id не существует
**`404 NOT FOUND`**
```json
{
  "message": "seller with id 10737 not found",
  "timestamp": "2026-05-22T06:16:20.605099",
  "details": []
}
```
</details>

### 4. Все транзакции продавца
<details>

```
GET /sellers/{id}transactions
```
#### Ответ 1. Стандартный
**`200 OK`**
```json
{
  "transactions": [
    {
      "id": 1073741824,
      "amount": 0,
      "paymentType": "CASH",
      "transactionDate": "2026-05-21T22:49:41.471Z",
      "sellerId": 1073741824
    }
  ]
}
```
#### Ответ 2. Продавца с таким id не существует
**`404 NOT FOUND`**
```json
{
  "message": "seller with id 10737 not found",
  "timestamp": "2026-05-22T06:16:20.605099",
  "details": []
}
```
</details>
</details>

## Аналитика
<details>

### Получить самого продуктивного продавца
<details>

Вывести самого продуктивного продавца в рамках дня, месяцы, квартала, года (самый
продуктивный, тот у которого сумма всех транзакции больше всех других продавцов).
В ответ может попасть несколько продавцов (если их суммы одинаковые)
```
GET /analytics/top-1-sellers?period=
```
| Параметр | Описание                           | Допустимые значения       |
|----------|------------------------------------|---------------------------|
| `period` | Единица времени как текущий период | DAY, MONTH, QUARTER, YEAR |
#### Ответ 1. Стандартный
**`200 OK`**
```json
{
  "sellers": [
    {
      "id": 1073741824,
      "name": "string",
      "contactInfo": "string",
      "registrationDate": "2026-05-22T00:14:46.727Z",
      "total": 0
    }
  ]
}
```
| Поле    | Описание                       | Тип     |
|---------|--------------------------------|---------|
| `total` | Сумма всех транзакций продавца | numeric |
</details>

### Получить список продавцов с суммой меньше указанной
<details>

Вывести список продавцов, у которых сумма всех транзакции за выбранный период
меньше переданного параметра суммы
```
GET /analytics/sellers-below-threhold?from={}?to={}?threshold={}
```
| Параметр    | Описание       | Тип                                        |
|-------------|----------------|--------------------------------------------|
| `from`      | Начало периода | дата и время в формате yyyy-MM-dd HH:mm:ss |
| `to`        | Конец периода  | дата и время в формате yyyy-MM-dd HH:mm:ss |
| `threshold` | Сумма          | десятичное число                           |
#### Ответ 1. Стандартный
**`200 OK`**
```json
{
  "sellers": [
    {
      "id": 1073741824,
      "name": "string",
      "contactInfo": "string",
      "registrationDate": "2026-05-22T00:14:46.727Z",
      "total": 0
    }
  ]
}
```
</details>
</details>
