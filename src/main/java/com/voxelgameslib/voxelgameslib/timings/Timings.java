package com.voxelgameslib.voxelgameslib.timings;

import lombok.extern.java.Log;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Small class that helps with timing stuff
 */
@Log
public class Timings {

    /**
     * Times a task, will print the result with log level finer
     *
     * @param name     the name of the task
     * @param executor the task to be timed
     */
    public static void time(String name, TimingsExecutor executor) {
        LocalDateTime start = LocalDateTime.now();
        executor.execute();
        LocalDateTime end = LocalDateTime.now();

        Duration duration = Duration.between(start, end);
        String time = LocalTime.MIDNIGHT.plus(duration)
                .format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"));
        log.finer("Timings: " + name + " took " + time);
    }
}