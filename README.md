# Приложение ShareIt

ShareIt — приложение, которое дает возможность арендовать вещи у других пользователей.

## Основные возможности приложения

* Предметы - Создание предметов, которые пользователь готов сдать в аренду.
* Заявки на предметы - Можно создавать заявку на необходимые предметы.
* Доступ к информации - Возможности для получения информации о предметах и бронированиях и
  редактированию ее.
* Комментарии - Возможность оставлять комментарии под конкретным предметом.

## Архитектура

Приложение состоит из 2 сервисов:

* Гейтвей — Собирает запросы извне и перенаправляет их в основной сервис. Возможно развернуть
  несколько копий гейтвея для распределения нагрузки.
* Основной сервис — Реализует основной функционал приложения.

## Инструкция по запуску приложения

Для корректной работы приложения необходимо наличие на компьютере Docker. Оба сервиса запускаются
следующей командой:

```Bash
mvn install
docker-compose up
```

## Используемые технологии

* Spring Boot
* Maven
* Lombok
* Hibernate ORM
* PostgreSQL
* Docker

### Бронирования.

<details>
  <summary> POST /bookings <br />
     Добавление нового запроса на бронирование вещи.
</summary>
Пример запроса:

```json
{
  "start": "2022-11-03 09:00:45",
  "end": "2022-11-10 12:00:00",
  "itemId": 1
}
```

Пример ответа:

```json
{
  "id": 1,
  "start": "2022-11-03 09:00:45",
  "end": "2022-11-10 12:00:00",
  "itemId": 1,
  "booker": 3,
  "item:id": 1,
  "item:name": "Газонокосилка",
  "status": "WAITING"
}
```

</details>  
<details>
  <summary> POST /bookings/{bookingId} <br />
     Одобрение или отклонение запроса на бронирование вещи.
</summary>
Пример запроса:

POST /bookings/{bookingId}?isApproved=true

Пример ответа:

```json
{
  "id": 1,
  "start": "2022-11-03 09:00:45",
  "end": "2022-11-10 12:00:00",
  "itemId": 1,
  "booker": 3,
  "item:id": 1,
  "item:name": "Газонокосилка",
  "status": "APPROVED"
}
```

</details> 
<details>
  <summary> GET /bookings/{bookingId} <br />
     Получение запроса на бронирование по его ID
</summary>

Пример ответа:

```json
{
  "id": 1,
  "start": "2022-11-03 09:00:45",
  "end": "2022-11-10 12:00:00",
  "itemId": 1,
  "booker": 3,
  "item:id": 1,
  "item:name": "Газонокосилка",
  "status": "APPROVED"
}
```

</details> 


<details>
  <summary> GET /bookings <br />
     Получение всех запросов на бронирование, поступивших от пользователя.
</summary>
</details> 


<details>
  <summary> GET /bookings/owner <br />
     Получение всех запросов на бронирование, поступивших к хозяину вещей.
</summary>
</details> 

### Предметы.

<details>
  <summary> POST /items <br />
     Добавление новой вещи.
</summary>
Пример запроса:

```json
{
  "name": "Газонокосилка",
  "description": "Электрическая",
  "available": true,
  "requestId": 1
}
```

Поле requestId необходимо, если предмет добавляется по запросу на бронирование.

Пример ответа:

```json
{
  "id": 1,
  "name": "Газонокосилка",
  "description": "Электрическая",
  "available": true,
  "requestId": 1
}
```

</details>

<details>
  <summary> POST /items/{itemId} <br />
     Редактирование вещи.
</summary>
Пример запроса:

```json
{
  "name": "Газонокосилка",
  "description": "Бензиновая",
  "available": true
}
```

Пример ответа:

```json
{
  "id": 1,
  "name": "Газонокосилка",
  "description": "Бензиновая",
  "available": true
}
```

</details>

<details>
  <summary> GET /items/{itemId} <br />
     Получение полной информации о предмете, доступно хозяину предмета.
</summary>

Пример ответа:

```json
{
  "id": 1,
  "name": "Газонокосилка",
  "description": "Бензиновая",
  "available": true,
  "comments": [
    {
      "id": 1,
      "text": "Хорошая косилка!",
      "authorName": "Артем",
      "created": "2022-11-03 10:00:45"
    }
  ],
  "lastBooking": {
    "id": 1,
    "bookerId": 2,
    "startDate": "2022-11-03 09:00:45"
  },
  "nextBooking": null,
  "requestId": 1
}
```
</details> 

<details>
  <summary> GET /items <br />
     Получение полной информации о предметах, доступно хозяину предмета.
</summary>
</details>

<details>
  <summary> GET /items/search <br />
    Поисковый запрос по названию и\или описанию предмета, возвращает список с пагинацией.
</summary>
</details>

<details>
  <summary> POST /items/{itemId}/comment <br />
     Добавление комментария к предмету.
</summary>

Пример запроса:

```json
{
  "text": "Хорошая косилка!"
}
```

Пример ответа:

```json
{
  "id": 1,
  "text": "Хорошая косилка!",
  "authorName": "Артем",
  "created": "2022-11-03 10:00:45"
}
```
</details>

### Запросы на предметы.

<details>
  <summary> POST /requests <br />
    Добавление нового запроса на предмет.
</summary>

Пример запроса:

```json
{
  "description": "Требуется газонокосилка!"
}
```

Пример ответа:

```json
{
  "id": 1,
  "description": "Требуется газонокосилка!",
  "requestor": {
    "id": 2
  },
  "created": "2022-11-03 09:00:00"
}
```
</details>

<details>
  <summary> GET /requests <br />
    Получение списка запросов пользователя. ID пользователя передается в заголовке.
</summary>
</details>

<details>
  <summary> GET /requests/{requestId} <br />
    Получение запроса на предмет по ID запроса.
</summary>
</details>

<details>
  <summary> GET /requests/all <br />
    Получение пагинированного списка всех существующих запросов на предметы. Запросы пользователя из списка исключаются.
</summary>
</details>

### Пользователи.

<details>
  <summary> GET /users <br />
    Получение пагинированного списка всех пользователей.
</summary>
</details>

<details>
  <summary> GET /users/{id} <br />
    Получение пользователя по его ID.
</summary>

Пример ответа:
```json
{
  "id": 2,
  "name": "Артем",
  "email": "tyoma@mail.ru"
}
```
</details>

<details>
  <summary> POST /users/{id} <br />
    Редактирование пользователя.
</summary>

Пример запроса:
```json
{
  "email": "tyoma@gmail.com"
}
```

Пример ответа:
```json
{
  "id": 2,
  "name": "Артем",
  "email": "tyoma@gmail.com"
}
```
</details>

<details>
  <summary> POST /users <br />
    Добавление нового пользователя.
</summary>

Пример запроса:
```json
{
  "name": "Артем",
  "email": "tyoma@mail.ru"
}
```

Пример ответа:
```json
{
  "id": 2,
  "name": "Артем",
  "email": "tyoma@mail.ru"
}
```
</details>

<details>
  <summary> DELETE /users/{id} <br />
    Удаление пользователя.
</summary>
</details>

### Что можно улучшить:

Провести рефактор кода, улучшить нейминг методов и названий полей.

Разделить API на публичную и авторизованную часть. Добавить администраторский функционал.