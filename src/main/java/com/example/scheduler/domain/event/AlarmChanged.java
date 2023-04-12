package com.example.scheduler.domain.event;

import com.example.scheduler.domain.enumeration.SelectedTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AlarmChanged {

    private Long AlarmId;
    private String userId;
    private String eventType;
    private String keyword;
    private List<String> excludeUrl = new ArrayList<>();
    private String siteUrl;
    private String description;
    private SelectedTime refeshTime;
    private Boolean vbEnabled;

    public AlarmChanged() {}
}
