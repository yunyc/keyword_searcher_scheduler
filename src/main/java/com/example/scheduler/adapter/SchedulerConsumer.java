package com.example.scheduler.adapter;

import com.example.scheduler.config.KafkaProperties;
import com.example.scheduler.domain.event.AlarmChanged;
import com.example.scheduler.service.SchedulerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SchedulerConsumer {

    private final Logger log = LoggerFactory.getLogger(SchedulerConsumer.class);
    private final AtomicBoolean closed = new AtomicBoolean(false);
    public static final String TOPIC = "topic_scheduler";
    private final KafkaProperties kafkaProperties;
    private KafkaConsumer<String, String> kafkaConsumer;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    private SchedulerService schedulerService;

    public SchedulerConsumer(KafkaProperties kafkaProperties, SchedulerService schedulerService) {
        this.kafkaProperties = kafkaProperties;
        this.schedulerService = schedulerService;
    }

    @PostConstruct
    public void start() {
        log.info("Kafka consumer starting ...");
        this.kafkaConsumer = new KafkaConsumer<>(kafkaProperties.getConsumerProps());
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        kafkaConsumer.subscribe(Collections.singleton(TOPIC));
        log.info("Kafka consumer started");

        executorService.execute(() -> {
            try {
                while (!closed.get()) {
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(3));
                    for (ConsumerRecord<String, String> record : records) {
                        log.info("Consumed message in {} : {}", TOPIC, record.value());
                        ObjectMapper objectMapper = new ObjectMapper();
                        AlarmChanged alarmChanged = objectMapper.readValue(record.value(), AlarmChanged.class);
                        schedulerService.processAlarmChanged(alarmChanged);
                    }
                }
                kafkaConsumer.commitSync();
            } catch (WakeupException e) {
                if (!closed.get()) {
                    throw e;
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                log.info("kafka consumer close");
                kafkaConsumer.close();
            }
        });
    }

    public KafkaConsumer<String, String> getKafkaConsumer() {
        return kafkaConsumer;
    }

    public void shutdown() {
        log.info("Shutdown Kafka consumer");
        closed.set(true);
        kafkaConsumer.wakeup();
    }
}
