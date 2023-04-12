package com.example.scheduler.domain.enumeration;

/**
 * The SelectedTime enumeration.
 */
public enum SelectedTime {
    ONE(60, "1분"),
    FIVE(300, "5분"),
    TEN(600, "10분"),
    THIRTY(1800, "30분");

    private Integer time;
    private String description;

    SelectedTime(Integer time, String description) {
        this.time = time;
        this.description = description;
    }

    public Integer getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }
}
