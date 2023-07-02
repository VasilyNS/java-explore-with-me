FROM amazoncorretto:11-alpine-jdk
COPY server/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
#TODO: Закомментить таймзону перед отправкой на гитхаб
#RUN apk add --no-cache tzdata
#ENV TZ=Asia/Krasnoyarsk