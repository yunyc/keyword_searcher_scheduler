package com.example.scheduler.domain.event;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NoticeChanged {

    private String content;
    private String userId;
    private String siteUrl;
    private Long alarmId;

    public NoticeChanged() {}
}
