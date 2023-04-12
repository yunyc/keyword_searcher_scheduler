package com.example.scheduler.service;

import com.example.scheduler.domain.event.AlarmChanged;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.concurrent.ExecutionException;
import org.quartz.SchedulerException;

public interface SchedulerService {
    void createScheduler(AlarmChanged alarmChanged) throws SchedulerException, ClassNotFoundException, NoSuchMethodException;

    void updateScheduler(AlarmChanged alarmChanged) throws SchedulerException;

    void deleteScheduler(AlarmChanged alarmChanged) throws SchedulerException;

    void processAlarmChanged(AlarmChanged alarmChanged)
        throws SchedulerException, ClassNotFoundException, NoSuchMethodException, ExecutionException, InterruptedException, JsonProcessingException;
}
