FROM maven:3.6.3-openjdk-17
RUN mkdir -p /job4j_mock/services/desc
WORKDIR /job4j_mock/services/desc
COPY . .
RUN mvn install -Dmaven.test.skip=true
EXPOSE 9902
CMD ["java", "-jar", "target/desc-1.0.0.jar"]
