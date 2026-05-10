package com.revtalent.revtalent;

import com.revtalent.revtalent.service.ChromaService;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RevtalentApplication {

    @Autowired
    private ChromaService chromaService;

    public static void main(String[] args) {

        SpringApplication.run(
                RevtalentApplication.class,
                args
        );
    }

    @PostConstruct
    public void init() {

        chromaService.createCollection();
    }
}