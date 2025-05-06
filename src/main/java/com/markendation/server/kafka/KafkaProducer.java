package com.markendation.server.kafka;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.markendation.server.classes.ImageModelEvent;
import com.markendation.server.classes.TextModelEvent;

@Service
public class KafkaProducer {

    @Value("${kafka.producer.topic}")
    String topic;

    private final KafkaTemplate<String, TextModelEvent> textTemplate;
    private final KafkaTemplate<String, ImageModelEvent> imageTemplate;

    public KafkaProducer(KafkaTemplate<String, TextModelEvent> textTemplate, KafkaTemplate<String, ImageModelEvent> imageTemplate) {
        this.textTemplate = textTemplate;
        this.imageTemplate = imageTemplate;
    }

    public void send(TextModelEvent event) {
        textTemplate.send(topic, event);
    }

    public void send(ImageModelEvent event) {
        imageTemplate.send(topic, event);
    }
}