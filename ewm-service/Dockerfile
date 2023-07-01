FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
# Закомментить таймзону перед отправкой на гитхаб
RUN apk add --no-cache tzdata
ENV TZ=Asia/Krasnoyarsk