package com.mattjtodd.example;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    ExecutorService executorService() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Work %d").setDaemon(true).build();

        return new ThreadPoolExecutor(3, 3, 0, TimeUnit.MILLISECONDS, new SynchronousQueue<>(), threadFactory);
    }
}
