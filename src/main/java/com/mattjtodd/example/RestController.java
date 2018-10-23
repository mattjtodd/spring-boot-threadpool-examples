package com.mattjtodd.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.buffer.BufferCounterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    private final ScheduledExecutorService scheduledExecutorService;

    @Autowired
    @Qualifier("counter")
    private CounterService counterService;

    RestController() {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(100);
    }

    @RequestMapping("/asyncNioDeferredResult")
    public DeferredResult<Boolean> asyncNioDeferredResult(@RequestParam(name = "sleep", defaultValue = "1000") int sleep) {
        DeferredResult<Boolean> result = new DeferredResult<>(100000L, Boolean.FALSE);

        scheduledExecutorService.schedule(() -> {

            IntStream
                    .range(0, sleep)
                    .mapToObj(__ -> UUID.randomUUID().toString())
                    .collect(toList());

            result.setResult(Boolean.TRUE);
        }, sleep, TimeUnit.MILLISECONDS);

        return result;
    }
}
