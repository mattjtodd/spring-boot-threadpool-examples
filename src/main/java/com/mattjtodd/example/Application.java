package com.mattjtodd.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.endpoint.MetricReaderPublicMetrics;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.repository.InMemoryMetricRepository;
import org.springframework.boot.actuate.metrics.rich.InMemoryRichGaugeRepository;
import org.springframework.boot.actuate.metrics.writer.DefaultCounterService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@SpringBootApplication
public class Application {

    private final InMemoryMetricRepository counterMetricRepository = new InMemoryMetricRepository();

    @Bean
    @Primary
    public InMemoryRichGaugeRepository inMemoryRichGaugeRepository() {
        return new InMemoryRichGaugeRepository();
    }

    @Bean("counter")
    public CounterService counterService() {
        return new DefaultCounterService(counterMetricRepository);
    }

    // bean must not be named metricReaderPublicMetrics, one with that name already exists and the other one silently wins
    @Bean
    public MetricReaderPublicMetrics counterMetricReaderPublicMetrics() {
        return new MetricReaderPublicMetrics(counterMetricRepository);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
