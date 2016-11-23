package com.zemiak.podcasts.domain;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.ejb.Timer;

public class ScheduleEntry {
    private final ZonedDateTime date;
    private final Timer timer;

    public ScheduleEntry(ZonedDateTime date, Timer timer) {
        this.date = date;
        this.timer = timer;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public Timer getTimer() {
        return timer;
    }

    public LocalDateTime getNearestRun() {
        return LocalDateTime.ofInstant(timer.getNextTimeout().toInstant(), ZoneId.of("UTC"));
    }

    public long getWaitingTime() {
        return timer.getTimeRemaining();
    }
}
