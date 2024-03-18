FROM maven:3.6.3-openjdk-17
RUN mkdir -p /job4j_mock/services/auth
WORKDIR /job4j_mock/services/auth
COPY . .
RUN mvn install -Dmaven.test.skip=true
EXPOSE 9900
CMD ["java", "-jar", "target/auth-1.0.0.jar"]