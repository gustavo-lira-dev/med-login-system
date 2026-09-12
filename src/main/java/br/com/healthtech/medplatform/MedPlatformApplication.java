package br.com.healthtech.medplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MedPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedPlatformApplication.class, args);
        System.out.println("Hello World!");
    }
}
