package com.mattjtodd.example;

import javaslang.control.Try;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.nonNull;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    private static final Logger LOG = Logger.getLogger(RestController.class.getName());

    private final ExecutorService executorService;

    RestController(ExecutorService executorService) {
        this.executorService = Objects.requireNonNull(executorService);
    }

    @RequestMapping("/synchronous")
    public Boolean synchronous(@RequestParam(name = "sleep", defaultValue = "1000") int sleep) {
        return logEntryExit(() -> work(sleep));
    }

    @RequestMapping("/asyncTaskExecutor")
    public Callable<Boolean> asyncTaskExecutor(@RequestParam(name = "sleep", defaultValue = "1000") int sleep) {
        return logEntryExit(() -> () -> work(sleep));
    }

    @RequestMapping("/asyncDeferredResult")
    public DeferredResult<Boolean> asyncDeferredResult(@RequestParam(name = "sleep", defaultValue = "1000") int sleep) {
        return logEntryExit(() -> {
            DeferredResult<Boolean> result = new DeferredResult<>(100000L, Boolean.FALSE);

            Try
                .of(() -> CompletableFuture.supplyAsync(() -> work(sleep), executorService))
                .map(future -> future.handle((workResult, thrown) -> nonNull(workResult) ? result.setResult(workResult) : result.setErrorResult(thrown)))
                .onFailure(result::setErrorResult);

            return result;
        });
    }

    @ExceptionHandler(RejectedExecutionException.class)
    private ResponseEntity<Void> handleResourceNotFoundException(RejectedExecutionException e) {
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .build();
    }

    private static <T> T logEntryExit(Supplier<T> work) {
        LOG.info("Controller Thread consumed");
        T result = work.get();
        LOG.info("Controller Thread returned");
        return result;
    }

    private static Boolean work(int sleep) {
        LOG.info("Starting work..");
        try {
            Thread.sleep(sleep);
            return true;
        } catch (InterruptedException e) {
            LOG.log(Level.SEVERE, "Sleep error", e);
            return false;
        }
        finally {
            LOG.info("Completed work..");
        }
    }
}
