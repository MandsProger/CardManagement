
**Система управления юанковскими картами**

## 📖 Описание проекта
Проект представляет собой **REST API для управления банковскими картами**, с возможностью создавать, блокировать, просматривать карты и осуществлять переводы между своими картами. Проект реализован на Java с использованием Spring Boot и Spring Security, с поддержкой JWT-аутентификации.

## 💳 Функционал
### Карты
- Создание и управление картами
- Просмотр информации о картах
- Переводы между своими картами
- Атрибуты карты:
  - Номер карты (зашифрован и отображается маской: `**** **** **** 1234`)
  - Владелец
  - Срок действия
  - Статус: Активна, Заблокирована, Истек срок
  - Баланс

### Пользователи и роли
- **ADMIN**:
  - Создает, блокирует, активирует и удаляет карты
  - Управляет пользователями
  - Видит все карты
- **USER**:
  - Просматривает свои карты (поиск + пагинация)
  - Запрашивает блокировку карты
  - Делает переводы между своими картами
  - Смотрит баланс

## 🔒 Безопасность
- Spring Security + JWT
- Ролевой доступ
- Шифрование данных
- Маскирование номеров карт

## 🗄 Работа с базой данных
- MySQL
- Миграции через Liquibase (`src/main/resources/db/migration`)

## 📦 Развертывание и запуск
1. **Клонировать репозиторий:**
   ```bash
   git clone https://github.com/MandsProger/CardManagement.git
   cd CardManagement

2. **Настроить базу данных MySQL** и указать параметры подключения в `application.properties`:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/card_db
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```
3. **Собрать и запустить проект:**

   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```
4. **Открыть документацию Swagger:**
   [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## 📚 Примеры API

### 1. Получение всех карт пользователей

**Запрос:**

```http
GET /api/cards/userCards/5
Authorization: Bearer <JWT_TOKEN>
```

**Ответ (200 OK):**

```json
[
  {
    "id": 1,
    "number": "**** **** **** 1234",
    "ownerUsername": "user1",
    "userId": "5",
    "expirationDate": "12/25",
    "status": "ACTIVE",
    "balance": 1500.00
  },
  {
    "id": 2,
    "number": "**** **** **** 5678",
    "ownerUsername": "user1",
    "userId": "5",
    "expirationDate": "08/24",
    "status": "BLOCKED",
    "balance": 500.00
  }
]
```

### 2. Создание новой карты (ADMIN)

**Запрос:**
```http
GET /api/cards/userCards/{userId}
Authorization: Bearer <JWT_TOKEN>

{
  "number": "3526467823454321",
  "userId": 3,
  "status": "string",
  "balance": 0
}
```

**Ответ (201 Created):**

```json
{
  "id": 3,
  "number": "**** **** **** 4321",
  "owner": "user",
  "expiryDate": "11/26",
  "status": "ACTIVE",
  "balance": 1000.00
}
```

### 3. Перевод между картами пользователя

**Запрос:**

```http
POST /api/cards/transfer
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "fromCardId": 1,
  "toCardId": 2,
  "amount": 200.00
}
```

**Ответ (200 OK):**

### 4. Блокировка карты (USER)

**Запрос:**

```http
POST /api/cards/block/{id}
Authorization: Bearer <JWT_TOKEN>
```

**Ответ (200 OK):**

## ⚙ Технологии

* Java 17+
* Spring Boot, Spring Security, Spring Data JPA
* JWT
* MySQL
* Liquibase
* Swagger / OpenAPI
