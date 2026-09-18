package com.vigilanza.pattuglie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GestionePattuglieApplication {
    public static void main(String[] args) {
        SpringApplication.run(GestionePattuglieApplication.class, args);
    }
}
