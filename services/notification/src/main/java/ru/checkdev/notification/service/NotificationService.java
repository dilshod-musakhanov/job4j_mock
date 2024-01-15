package ru.checkdev.notification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.domain.Notify;


@Service
public class NotificationService {

    private final KafkaTemplate<String, Notify> kafkaTemplate;

    @Value("${spring.kafka.topic.notify}")
    private String kafkaTopic;

    public NotificationService(KafkaTemplate<String, Notify> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void put(final Notify notify) {
        kafkaTemplate.send(kafkaTopic, notify);
    }

}
