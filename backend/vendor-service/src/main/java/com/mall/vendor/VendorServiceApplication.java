package com.mall.vendor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableJpaAuditing
@EnableKafka
public class VendorServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(VendorServiceApplication.class, args);
    }
}
