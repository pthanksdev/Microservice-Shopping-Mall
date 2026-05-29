package com.mall.wishlist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableJpaAuditing
@EnableKafka
public class WishlistServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WishlistServiceApplication.class, args);
    }
}
