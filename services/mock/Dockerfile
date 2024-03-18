FROM maven:3.6.3-openjdk-17
RUN mkdir -p /job4j_mock/services/mock
WORKDIR /job4j_mock/services/mock
COPY . .
RUN mvn install -Dmaven.test.skip=true
EXPOSE 9912
CMD ["java", "-jar", "target/mock-1.0.0.jar"]