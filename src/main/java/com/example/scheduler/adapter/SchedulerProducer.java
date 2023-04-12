package com.example.scheduler.adapter;

import com.example.scheduler.domain.event.NoticeChanged;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface SchedulerProducer {
    void sendNoticeCreateEvent(NoticeChanged noticeChanged) throws ExecutionException, InterruptedException, JsonProcessingException;
}
