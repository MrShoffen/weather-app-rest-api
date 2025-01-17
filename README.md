# Проект “Погода”

Проект “Погода” представляет собой простой сервис для отслеживания текущей погоды.

Возможно просто получать погоду по необходимым координатам без авторизации. Так же реализован функционал по регистрации
пользователей и сохранению локаций в своем личном кабинете для остлеживания погоды.

Проект реализован в рамках работы над Роадмапом Серея
Жукова - https://zhukovsd.github.io/java-backend-learning-course/projects/weather-viewer/

## Отхождения от ТЗ

В качестве фронтенда используется не Thymeleaf и MVC - фронтенд написан отдельно на React, а данный бэкенд сервис
предоставляет REST эндпоинты, с которыми работает фронтенд.

Фронт так же доступен на гитхабе - https://github.com/MrShoffen/weather-app-react-frontend

Формат обмена данными между фронтом и бэком, а так же доступные эндпоинты, будут описаны ниже.

## Сборка и запуск проекта на локальном сервере

Доступно 2 варианта запуска - отдельно бэкенд сервис или совместно с фронтендом. Для обоих вариантов понадобится docker
compose, java 21 и
OpenWeatherApi ключ

### Запуск только бэкенда

1) После клонирования репозитория необходимо установить переменную WEATHER_KEY в файле vars.env на свой OpenWeatherApi
   ключ (не забудьте убрать решетку :)

```
WEATHER_KEY=
```

остальные пропертис связаны с базой данных, если они не конфликтуют с вашими - можно оставить как есть.

2) Сделайте файл gradlew исполняемым:
```
sudo chmod +x ./gradlew
```
далее можно прогнать тесты, чтобы убедиться, что всё работает
```
./gradlew test
```

3) Далее из корневой папки проекта просто запустите docker compose

```
docker compose up -d
```

В результате? в двух отдельных контейнерах запустится база данных Postgres и сам бэкенд .

бэк будет доступен по localhost:8081/weather/api/ (эндпоинты постмана для проверки так же в корне проекта)

### Запуск совместно с фронтендом

1) После клонирования репозитория снова установите ключ WEATHER_KEY и затем запустите скрипт ./deploy.sh (скорее всего, понадобиться ввести пароль от уч. записи
   линукс)

чтобы сделать скрипт исполняемым:
```
sudo chmod +x ./deploy.sh
```
2) 
   В результате будет поднятно 5 контейнеров:

- раздача статики фронтенда через nginx
- бэкенд spring boot
- база данных для бэкенда
- небольшой rest сервис для работы автозаполнения (исходники можно посмотреть тут https://github.com/MrShoffen/cities_api )
- nginx для проксирования между всеми контейнерами

Далее приложение будет доступно по ссылку localhost/weather-app/

бэкенд - по эндпоинту localhost/api/weather/

----
## Описание работы
В приложении есть 2 вида эндпоинтов - одни доступны в независимости от авторизации (получение сохраненных аватаров, запрос локаций и погоды для гостевого просмотра и энпоинты, собственно, для авторизации)

Второй вид эндпоинтов - доступные только после успешной авторизации.

### Описание авторизации

После аутентификации клиент получает куки с именем CUSTOM_SESSION - далее ко всем защищенным эндпоинтам необходимо обращаться вместе с этим куки.
На стороне бэкенда при каждом запросе через AuthorizationInterceptor проверяется наличие данного куки и его валидность по базе данных.

Если куки валидный - разрешается доступ к защищенным страницам для определенного пользователя. Защищенные эндпоинты расположены в пакете  "authenticated".

Для простоты так же создан AuthorizedUserArgumentResolver - который с помощью куки извлекает из базы данных пользователя, которорму этот куки соответствует.
Чтобы в защищенных контроллерах получить доступ к авторизованному пользователю достаточно воспользовать аннотацией @AuthorizedUser


----

## Описание методов REST API
### Публичные эндпоинты (доступны и при авторизации и без)

#### Регистрация - POST /weather/api/auth/registration

Принимает:
```json
{
   "username" : "username",
   "password" : "password",
   "avatarUrl" : "avatar url"
}

```
_!!! avatarUrl - путь к аватару относительно корневого эндпоинта.
Например:  '/weather/api/images/6dafb1e3-08c4-42ca-965b-33919f115fc7.jpg' Имя загружаемой картинке присвается сервером_

При успешной регистрации возвращается json с данными пользователя:
```json
201 Created
{
   "id": 3,
   "username": "username",
   "avatarUrl": "avatar url"
}
```

Если допущена имя пользователя уже занято - поступит соответстующий ProblemDetail
```json
409 Conflict
{
    "type": "about:blank",
    "title": "UserAlreadyExistsException",
    "status": 409,
    "detail": "User with username 'username' already exists!",
    "instance": "/weather/api/auth/registration"
}
```

При некорректной валидации - тоже будет получена ошибка 
```json
400 Bad Reques
{
    "type": "about:blank",
    "title": "MethodArgumentNotValidException",
    "status": 400,
    "detail": "Incorrect symbols in username!",
    "instance": "/weather/api/auth/registration"
}
```

---
#### Вход - POST /weather/api/auth/login
Принимает:
```json
{
   "username" : "username",
   "password" : "password"
}
```

При успешном входе возвращает данные:

```json
200 OK
{
   "id": 3,
   "username": "username",
   "avatarUrl": "avatar url"
}
```
_!!! Так же после успешного входа со стороны бэкенда устанавливается куки CUSTOM_SESSION={UUID} - все дальнейшие запросы должны отправляться с данным куки (постман, например,  автоматом устанавливает куки к последующим запросам)_


Если пользователь уже вошел и получил куки для авторизации - повторная попытка зарегистрироваться или войти вернет ошибку:
```json
403 Forbidden
{
    "type": "about:blank",
    "title": "UserAlreadyAuthorizedException",
    "status": 403,
    "detail": "You are already authorized!",
    "instance": "/weather/api/auth/login"
}

```
Аналогично регистрации - при ошибках в вводе логина или пароля - будет получена ошибка с сообщением о валидации
```json
400 Bad Reques
{
    "type": "about:blank",
    "title": "MethodArgumentNotValidException",
    "status": 400,
    "detail": "Incorrect symbols in username!",
    "instance": "/weather/api/auth/login"
}
```

---

#### Получение локаций по имени для гостя - GET /weather/api/locations?name=Saint Petersburg

Параметр поиска (name) передается через query параметр в строке запроса.

Ответ - массив найденных локаций:

```json
200 OK
[
   {
      "id": null,
      "name": "Saint Petersburg",
      "state": "Saint Petersburg",
      "country": "RU",
      "lat": 59.938732,
      "lon": 30.316229
   }
]
```
id устанавливается в методе для сохранения локации (после сохранения в базе данных). не стал делать отдельно дто с айди и без

При некорректных параметрах вернетя ошибка валидации

```json
GET: /weather/api/locations?name=

400 Bad Reques
{
    "type": "about:blank",
    "title": "ConstraintViolationException",
    "status": 400,
    "detail": "getLocations.name: Name can't be empty!",
    "instance": "/weather/api/locations"
}
```

---

#### Получение погоды по координатам для гостя - GET /weather/api/weather?lat=59.938732&lon=30.316229
Параметры координат (lat, lon) передаются через query параметр в строке запроса.



Ответ - погода для данных координат:
```json
200 OK

{
   "weather": [
      {
         "id": 804,
         "main": "Clouds",
         "description": "overcast clouds",
         "icon": "04n"
      }
   ],
   "main": {
      "pressure": 1015,
      "temp": 2.77,
      "feels_like": -1.45,
      "temp_min": 2.04,
      "temp_max": 2.77,
      "humidity": 92
   },
   "wind": {
      "speed": 5.0,
      "deg": 240
   },
   "clouds": {
      "all": 100
   }
}
```

При некорректный данных - ошибка валидации:
```json
GET /weather/api/weather?lat=120&lon=30.316229

400 Bad Reques
{
   "type": "about:blank",
   "title": "ConstraintViolationException",
   "status": 400,
   "detail": "getLocations.lat: Latitude must be a number between -90 and 90.",
   "instance": "/weather/api/weather"
}
```

---

### Защищенные эндпоинты (доступны только после авторизации, отправляются с куки)

Попытка отправить запрос на любой защищенный эндпоинт без авторизации приведет к следующей ошибке

```json
401 Unauthorized
{
   "type": "about:blank",
   "title": "UserUnauthorizedException",
   "status": 401,
   "detail": "Permission denied! Only for authorized users!",
   "instance": "/weather/api/user"
}

```

Если при любом запросе бэкенд обнаруживает, что сессия пользователя истекла - возвращается соответствующая ошибка
. И сессия удаляется из БД
```json
401 Unauthorized

{
    "type": "about:blank",
    "title": "SessionExpiredException",
    "status": 401,
    "detail": "Your session has expired! Please login again.",
    "instance": "/weather/api/user/locations"
}
```
Самый распространенный случай - у пользователя куки еще живет, но сессия истекла (т.к. куки устанавилваются долгоживущие).

---
#### Выход из аккаунта - POST /weather/api/auth/logout
При корректном выходе - возвращается 204 и бэкенд удаляет куки CUSTOM_SESSION у пользователя

```json
204 No Content
```

---
#### Данные пользователя - GET /weather/api/user
В ответ поступают данные о текущем авторизованном пользователе
```json
200 OK
{
    "id": 3,
    "username": "username",
    "avatarUrl": "avatar url"
}
```
---

#### Именение данных пользователя - PATCH /weather/api/user/profile

Принимает:

```json
{
   "newUsername" : "username_two",
   "newAvatarUrl" : "new_avatar"
}
```
Данные в базе данных обновляются - и приходит ответ с новыми данными:
```json
200 OK
{
    "id": 3,
    "username": "username_two",
    "avatarUrl": "new_avatar"
}
```
Правила валидации входных данных те же, что и для регистрации/входа - возвращаются те же ошибки. Так же вовзращается ошибка
если новое имя уже занято


---

#### Именение пароля пользователя - PATCH /weather/api/user/password
Принимает:

```json
{
   "oldPassword" : "password",
   "newPassword" : "password2"
}
```
Данные в базе данных обновляются - и приходит ответ с данными пользователя:
```json
200 OK
{
   "id": 3,
   "username": "username",
   "avatarUrl": "new_avatar"
}
```
Правила валидации входных данных те же, что и для регистрации/входа - возвращаются те же ошибки. Так же вовзращается ошибка
если старый пароль неверный:
```json
401 Unauthorized
{
    "type": "about:blank",
    "title": "IncorrectPasswordException",
    "status": 401,
    "detail": "Incorrect password!",
    "instance": "/weather/api/user/password"
}
```
---

#### Удаление пользователя - DELETE /weather/api/user

Gри успешном удалении -  пользователь удаляется из базы данных, а также стираются все активные сессии данного пользователя и возвращается статус 204

```json
204 No Content
```

---
#### Сохранение локации - POST /weather/api/user/locations

Принимает:

```json
{
   "name": "Moscow",
   "state": "Moscow",
   "country": "RU",
   "lat": 55.7504461,
   "lon": 37.6174943
}
```

При успешном сохранении - возвращается локация, уже с айди (в таблице локаций)
```json
201 Created
{
    "id": 2,
    "name": "Moscow",
    "state": "Moscow",
    "country": "RU",
    "lat": 55.7504461,
    "lon": 37.6174943
}
```

При ошибках валидации - возвращается ошибка. При попытке сохранить уже сохраненную (для данного пользователя) локацию - тоже ошибка:
```json
409 Conflict
{
    "type": "about:blank",
    "title": "LocationAlreadySavedException",
    "status": 409,
    "detail": "This location is already saved",
    "instance": "/weather/api/user/locations"
}
```

---

#### Получить конкретную локацию по айди - GET /weather/api/user/locations/2

Айди локации передается как path variable (в данном случае 2)


Если локация у данного пользователя сохранена - возращает её данные: 
```json
200 OK
{
   "id": 2,
   "name": "Moscow",
   "state": "Moscow",
   "country": "RU",
   "lat": 55.7504461,
   "lon": 37.6174943
}
```

Если у данного пользователя локация не сохранена - возвращает соответствующую ошибку:
```json
404 Not Found
{
    "type": "about:blank",
    "title": "LocationNotFoundException",
    "status": 404,
    "detail": "Location not found",
    "instance": "/weather/api/user/locations/8"
}
```


---

#### Удалить локацию из сохраненных по айди - DELETE /weather/api/user/locations/2
Айди локации передается как path variable (в данном случае 2)

```json
{
   "oldPassword" : "password",
   "newPassword" : "password2"
}
```

Если локация у данного пользователя сохранена - просходит удаление и возвращается 204:
```json
204 No Content
```

Если у данного пользователя локация не сохранена - возвращает соответствующую ошибку (при удалении проверяется, принадлежит ли локация данному пользвателю):
```json
404 Not Found
{
   "type": "about:blank",
   "title": "LocationNotFoundException",
   "status": 404,
   "detail": "Location not found",
   "instance": "/weather/api/user/locations/8"
}
```


---

####  Получение списка всех локаций вместе с погодой - GET /weather/api/user/locations/weather

Возвращает массив всех локаций пользователя вместе с погодой

```json
200 OK
[
   {
      "location": {
         "id": 4,
         "name": "Moscow",
         "state": "Moscow",
         "country": "RU",
         "lat": 55.7504461,
         "lon": 37.6174943
      },
      "weather": {
         "weather": [
            {
               "id": 804,
               "main": "Clouds",
               "description": "overcast clouds",
               "icon": "04n"
            }
         ],
         "main": {
            "pressure": 1018,
            "temp": 3.1,
            "feels_like": -1.14,
            "temp_min": 2.29,
            "temp_max": 3.34,
            "humidity": 79
         },
         "wind": {
            "speed": 5.2,
            "deg": 331
         },
         "clouds": {
            "all": 99
         }
      }
   },
   {
      "location": {
         "id": 5,
         "name": "Mytishi",
         "state": "Moscow",
         "country": "RU",
         "lat": 51.7504461,
         "lon": 36.6174943
      },
      "weather": {
         "weather": [
            {
               "id": 804,
               "main": "Clouds",
               "description": "overcast clouds",
               "icon": "04n"
            }
         ],
         "main": {
            "pressure": 1021,
            "temp": 1.19,
            "feels_like": -3.59,
            "temp_min": 1.19,
            "temp_max": 1.19,
            "humidity": 90
         },
         "wind": {
            "speed": 5.3,
            "deg": 319
         },
         "clouds": {
            "all": 99
         }
      }
   }
]
```
---

```

JSON ответ после обновления счета в матче

```json
{
  "firstPlayer": "First Name",
  "secondPlayer": "Second Name",
  "winner": null,
  "ended": false,
  "inTiebreak": false,
  "sets": {
    "TWO": [
      0,
      0,
      0
    ],
    "ONE": [
      0,
      0,
      0
    ]
  },
  "currentPoints": {
    "TWO": 0,
    "ONE": 1
  }
}
```
