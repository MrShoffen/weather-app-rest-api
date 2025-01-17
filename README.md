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

2) Далее из корневой папки проекта просто запустите docker compose

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
   В результате будет поднятно 4 контейнера:

- фронтенд через nginx
- бэкенд spring boot
- база данных для бэкенда
- небольшой rest сервис для работы автозаполнения (исходники можно посмотреть тут https://github.com/MrShoffen/cities_api )

Далее приложение будет доступно по ссылку localhost/weather-app/

----

## Описание методов REST API

JSON ответ со страницей матчей (эндпоинт GET /api/matches?page_number=1&page_size=10&player_name= )

```json
{
  "entities": [
    {
      "id": 1,
      "firstPlayer": "Ugo Humbert",
      "secondPlayer": "Sebastian Baez",
      "winner": "Sebastian Baez"
    },
    ...
    ...
    {
      "id": 10,
      "firstPlayer": "Ugo Humbert",
      "secondPlayer": "Karen Khachanov",
      "winner": "Ugo Humbert"
    }
  ],
  "pageNumber": 1,
  "pageSize": 10,
  "totalPages": 7
}
```

---
JSON ответ со страницей игроков (эндпоинт GET /api/players?page_number=1&page_size=10&player_name= :

```json
{
  "entities": [
    {
      "id": 1,
      "name": "Novak Djokovic",
      "matchesPlayed": 6,
      "matchesWon": 1
    }
    ...
    ...
    {
      "id": 10,
      "name": "Taylor Harry Fritz",
      "matchesPlayed": 3,
      "matchesWon": 0
    }
  ],
  "pageNumber": 1,
  "pageSize": 10,
  "totalPages": 4
}
```

---
JSON ответ со страницей завершенного матча (энпоинт GET /api/finished-match?id=1)

```json
{
  "id": 1,
  "firstPlayer": "Ugo Humbert",
  "secondPlayer": "Sebastian Baez",
  "winner": "Sebastian Baez"
}
```

---
JSON запрос на создание нового матча (эндпоинт POST /api/new-match)

```json
{
  "firstPlayer": "First Name",
  "secondPlayer": "Second Name"
}
```

---
JSON запрос при выигрывании очка (эндпоинт POST /api/match-score?uuid=321c75ce-e727-47b3-b7dc-97b9d7a503b5)

```json
{
  "pointWinner": "PointWinnerName"
}
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
