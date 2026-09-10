package br.com.healthtech.medplatform;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MedPlatformApplication implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Hello World!");
    }

    public static void main(String[] args) {
        SpringApplication.run(MedPlatformApplication.class, args);
    }
}
