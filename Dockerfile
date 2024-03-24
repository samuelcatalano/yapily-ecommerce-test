# Building stage
FROM openjdk:17-jdk-slim as builder

WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN apt-get update && \
    apt-get install -y maven && \
    mvn clean package -DskipTests && \
    apt-get remove -y maven && \
    apt-get autoremove -y && \
    apt-get clean

# Running stage
FROM openjdk:17-jdk-slim

MAINTAINER Samuel Catalano <samuel.catalano@gmail.com>

ENV TZ=Europe/London
ENV LC_ALL en_GB.UTF-8
ENV LANG en_GB.UTF-8
ENV LANGUAGE en_GB.UTF-8

RUN mkdir -p /usr/share/yapily && \
mkdir /var/run/yapily && \
mkdir /var/log/yapily

COPY --from=builder /app/target/yapily-e-commerce-api-1.0.0.jar /usr/share/leaseloco/yapily-e-commerce-api-1.0.0.jar

WORKDIR /usr/share/leaseloco/
EXPOSE 8080 8787 5432

CMD ["java","-Djava.security.egd=file:/dev/./urandom", "-Dfile.encoding=UTF-8", "-jar","yapily-e-commerce-api-1.0.0.jar"]
