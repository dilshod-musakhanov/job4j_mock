package ru.checkdev.notification.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.domain.Notify;

@Service
public class KafkaNotificationConsumer {

    private final TemplateService templateService;

    public KafkaNotificationConsumer(TemplateService templateService) {
        this.templateService = templateService;
    }

    @KafkaListener(topics = "${spring.kafka.topic.notify}", groupId ="${spring.kafka.consumer.group-id}")
    public void processNotify(Notify notify) {
        templateService.send(notify);
    }
}
