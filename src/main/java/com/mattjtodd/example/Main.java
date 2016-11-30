package com.mattjtodd.example;

import javaslang.control.Try;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {

        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");

        ExecutorService executorService = Executors.newCachedThreadPool();

        RestTemplate template = new RestTemplate();

//        String path = "synchronous";
//        String path = "asyncTaskExecutor";
        String path = "asyncDeferredResult";

        List<CompletableFuture<?>> collect = IntStream
            .range(0, 10)
            .mapToObj(count -> CompletableFuture
                .supplyAsync(() -> Try.of(() -> template.getForEntity("http://localhost:8080/" + path + "?sleep=10000", Boolean.class)), executorService))
            .map(future -> future.thenAccept(System.out::println))
            .collect(Collectors.toList());

        CompletableFuture.allOf(collect.toArray(new CompletableFuture<?>[collect.size()])).join();

        executorService.shutdown();
    }
}
