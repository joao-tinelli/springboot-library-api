# build
FROM maven:3.9.11-amazoncorretto-17-al2023 as build
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

# run
FROM amazoncorretto:17.0.5
WORKDIR /app

COPY --from=build ./build/target/*.jar ./libraryapi.jar

EXPOSE 8080
EXPOSE 9090

ENV DATASOURCE_URL=''
ENV DATASOURCE_USERNAME=''
ENV DATASOURCE_PASSWORD=''
ENV GOOGLE_CLIENT_ID='client-id'
ENV GOOGLE_CLIENT_SECRET='client-secret'

ENV SPRING_PROFILES_ACTIVE='production'
ENV TZ='America/Sao_Paulo'

ENTRYPOINT java -jar libraryapi.jar

