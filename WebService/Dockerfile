FROM gradle:6.6.1-jdk8

ENV WORKINGDIR=/home/gradle/src 

WORKDIR ${WORKINGDIR} 

RUN mkdir -p ${WORKINGDIR} && chmod 666 ${WORKINGDIR}

COPY --chown=gradle:gradle . ${WORKINGDIR}

RUN apt-get update && apt-get install -y netcat

RUN chmod +x /home/gradle/src/waitingForRabbit.sh

ENTRYPOINT [ "/home/gradle/src/waitingForRabbit.sh" ]
