package com.example.scheduler.adapter;

import com.example.scheduler.config.KafkaProperties;
import com.example.scheduler.domain.event.AlarmChanged;
import com.example.scheduler.domain.event.NoticeChanged;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SchedulerProducerImpl implements SchedulerProducer {

    private final Logger log = LoggerFactory.getLogger(SchedulerProducerImpl.class);
    private static final String TOPIC_ALARM = "topic_alarm";

    private final KafkaProperties kafkaProperties;
    private KafkaProducer<String, String> producer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SchedulerProducerImpl(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @PostConstruct
    public void initialize() {
        log.info("Kafka producer initializing...");
        this.producer = new KafkaProducer<>(kafkaProperties.getProducerProps());
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        log.info("Kafka producer initialized");
    }

    @Override
    public void sendNoticeCreateEvent(NoticeChanged noticeChanged)
        throws ExecutionException, InterruptedException, JsonProcessingException {
        log.info("sendNoticeCreateEvent start");
        String message = objectMapper.writeValueAsString(noticeChanged);
        producer.send(new ProducerRecord<>(TOPIC_ALARM, message)).get();
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutdown Kafka producer");
        producer.close();
    }
}
