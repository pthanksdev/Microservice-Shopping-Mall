package com.mall.discount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableJpaAuditing
@EnableKafka
public class DiscountServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscountServiceApplication.class, args);
    }
}
