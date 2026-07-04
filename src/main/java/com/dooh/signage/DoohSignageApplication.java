package com.dooh.signage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DoohSignageApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoohSignageApplication.class, args);
    }
}