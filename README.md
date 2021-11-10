# Nomi Cose Città
This project has the goal to reproduce one of the most famous and simple game ever invented: “Nomi Cose e Città”. The game is payed worldwide and assumes a different name according to the language spoken in each country:  “boy girl animal”, “Alto el lápiz”, “Jeu du baccalauréat “... but the mechanisms it’s always the same: write a word for each category the fastest possible. 

## Features
- Create a game and choose it's settings.
- Share your GameID to let your friends join the game. 
- Start the game and be the fastest. 🚀

## Tech

This project uses a number of open source tecnologies: 
- [Vert.X] - a tool-kit for writing sophisticated modern web applications and HTTP microservices.
- [SockJS] - a browser JavaScript library that provides a WebSocket-like object.
- [jQuery] - a fast, small, and feature-rich JavaScript library.
- [Materialize] - a modern responsive CSS framework based on Material Design by Google.
- [RabbitMQ] - the most widely deployed open source message broker.
- [Gradle] - to accelerate developer productivity.
- [Docker] - an open platform for developing, shipping, and running applications.


## Installation

Nomi Cose Città requires [Docker] to run.

Once cloned the repository: 

```sh
cd <projectFolder> 
docker compose up
```

By default, Docker will expose port 8080, so to verify the deployment you only need to navigate to http://localhost:8080 to see the magic happened. 

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)
   [Vert.x]: <https://vertx.io>
   [RabbitMQ]: <https://www.rabbitmq.com>
   [Gradle]: <https://gradle.org>
   [SockJS]: <https://github.com/sockjs/sockjs-client>
   [jQuery]: <http://jquery.com>
   [Materialize]: <https://materializecss.com>
   [Docker]: <https://www.docker.com>
