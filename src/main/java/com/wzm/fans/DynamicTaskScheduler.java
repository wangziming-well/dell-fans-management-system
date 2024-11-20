package com.wzm.fans;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

@Component
public class DynamicTaskScheduler {

    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledFuture;


    public DynamicTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void startTask() {
        if (scheduledFuture == null || scheduledFuture.isCancelled()) {
            scheduledFuture = taskScheduler.scheduleAtFixedRate(this::runTask, Duration.ofSeconds(5));
            System.out.println("Scheduled task started");
        }
    }

    public void stopTask() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(false);
            System.out.println("Scheduled task stopped");
        }
    }

    private void runTask() {
        System.out.println("Task is running...");
    }
}
