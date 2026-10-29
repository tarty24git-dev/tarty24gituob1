package com.cth.datamatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DataMatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataMatchApplication.class, args);
    }
}
