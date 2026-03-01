
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-Xmx256m", "-Xms128m", "-jar", "app.jar"]

#FROM eclipse-temurin:17-jre-alpine
#WORKDIR /app
#COPY target/*.jar app.jar
#EXPOSE 8081
#ENTRYPOINT ["java", "-jar", "app.jar"]

#FROM eclipse-temurin:17-jdk-alpine
#VOLUME /tmp
#COPY target/*.jar app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]
