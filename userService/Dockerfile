FROM openjdk:8

COPY . /home/myapp/userService

WORKDIR /home/myapp/userService

RUN /bin/bash -c chmod +x gradlew

CMD  gradle wrapper

CMD  ./gradlew run
