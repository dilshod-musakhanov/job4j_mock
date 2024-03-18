FROM maven:3.6.3-openjdk-17
RUN mkdir -p /job4j_mock/services/site
WORKDIR /job4j_mock/services/site
COPY . .
RUN mvn install -Dmaven.test.skip=true
EXPOSE 8080
CMD ["java", "-jar", "target/site-1.0.0.jar"]
