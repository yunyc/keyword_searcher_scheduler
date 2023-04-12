package com.example.scheduler.service;

import com.example.scheduler.domain.event.AlarmChanged;
import org.quartz.SchedulerException;

public interface SchedulerService {
    void createScheduler(AlarmChanged alarmChanged) throws SchedulerException, ClassNotFoundException, NoSuchMethodException;

    void updateScheduler(AlarmChanged alarmChanged) throws SchedulerException;

    void deleteScheduler(AlarmChanged alarmChanged) throws SchedulerException;

    void processAlarmChanged(AlarmChanged alarmChanged) throws SchedulerException, ClassNotFoundException, NoSuchMethodException;
}
