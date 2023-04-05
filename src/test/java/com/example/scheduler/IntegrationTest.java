package com.example.scheduler;

import com.example.scheduler.SchedulerApp;
import com.example.scheduler.config.AsyncSyncConfiguration;
import com.example.scheduler.config.EmbeddedKafka;
import com.example.scheduler.config.EmbeddedSQL;
import com.example.scheduler.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { SchedulerApp.class, AsyncSyncConfiguration.class, TestSecurityConfiguration.class })
@EmbeddedKafka
@EmbeddedSQL
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public @interface IntegrationTest {
}
