package com.revtalent.revtalent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication()
public class RevtalentApplication {

    public static void main(String[] args) {
        SpringApplication.run(RevtalentApplication.class, args);
    }
}