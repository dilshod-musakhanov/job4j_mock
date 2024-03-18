FROM maven:3.6.3-openjdk-17
RUN mkdir -p /job4j_mock/services/notification
WORKDIR /job4j_mock/services/notification
COPY . .
RUN mvn install -Dmaven.test.skip=true
EXPOSE 9920
CMD ["java", "-jar", "target/notification-1.0.0.jar"]