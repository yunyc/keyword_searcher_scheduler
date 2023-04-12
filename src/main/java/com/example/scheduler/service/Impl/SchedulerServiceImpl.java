package com.example.scheduler.service.Impl;

import com.example.scheduler.adapter.SchedulerProducer;
import com.example.scheduler.domain.event.AlarmChanged;
import com.example.scheduler.domain.event.NoticeChanged;
import com.example.scheduler.scheduler.WebCrawler;
import com.example.scheduler.service.SchedulerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.concurrent.ExecutionException;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.stereotype.Service;

@Service
public class SchedulerServiceImpl implements SchedulerService {

    private final Logger log = LoggerFactory.getLogger(SchedulerServiceImpl.class);

    private final Scheduler scheduler;
    private final SchedulerProducer schedulerProducer;

    public SchedulerServiceImpl(Scheduler scheduler, SchedulerProducer schedulerProducer) {
        this.scheduler = scheduler;
        this.schedulerProducer = schedulerProducer;
    }

    @Override
    public void createScheduler(AlarmChanged alarmChanged) throws SchedulerException, ClassNotFoundException, NoSuchMethodException {
        MethodInvokingJobDetailFactoryBean jobDetailBean = createJobDetailBean(alarmChanged);
        Trigger trigger = createTrigger(alarmChanged);

        scheduler.scheduleJob((JobDetail) jobDetailBean.getObject(), trigger);
    }

    @Override
    public void updateScheduler(AlarmChanged alarmChanged) throws SchedulerException {
        Trigger trigger = createTrigger(alarmChanged);

        scheduler.rescheduleJob(new TriggerKey(alarmChanged.getAlarmId().toString()), trigger);
    }

    @Override
    public void deleteScheduler(AlarmChanged alarmChanged) throws SchedulerException {
        scheduler.deleteJob(new JobKey(alarmChanged.getAlarmId().toString()));
    }

    @Override
    public void processAlarmChanged(AlarmChanged alarmChanged)
        throws SchedulerException, ClassNotFoundException, NoSuchMethodException, ExecutionException, InterruptedException, JsonProcessingException {
        String eventType = alarmChanged.getEventType();
        switch (eventType) {
            case "NEW_ALARM":
                //createScheduler(alarmChanged);
                schedulerProducer.sendNoticeCreateEvent(new NoticeChanged("content", "id", "site", null, 1L));
                break;
            case "DELETE_ALARM":
                deleteScheduler(alarmChanged);
                break;
            case "UPDATE_ALARM":
                updateScheduler(alarmChanged);
                break;
        }
    }

    public MethodInvokingJobDetailFactoryBean createJobDetailBean(AlarmChanged alarmChanged)
        throws ClassNotFoundException, NoSuchMethodException {
        MethodInvokingJobDetailFactoryBean jobDetailBean = new MethodInvokingJobDetailFactoryBean();
        jobDetailBean.setTargetObject(new WebCrawler(schedulerProducer, alarmChanged));
        jobDetailBean.setTargetMethod("findKeyword");
        jobDetailBean.setName(alarmChanged.getAlarmId().toString());
        jobDetailBean.afterPropertiesSet();
        return jobDetailBean;
    }

    public Trigger createTrigger(AlarmChanged alarmChanged) {
        Trigger trigger = TriggerBuilder
            .newTrigger()
            .withIdentity(alarmChanged.getAlarmId().toString())
            .startNow()
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(alarmChanged.getRefeshTime().getTime()).repeatForever()
            )
            .build();
        return trigger;
    }
}
