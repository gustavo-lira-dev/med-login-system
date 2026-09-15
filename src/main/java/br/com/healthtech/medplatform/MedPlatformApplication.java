package br.com.healthtech.medplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@ComponentScan(basePackages = "br.com.healthtech.medplatform")
public class MedPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedPlatformApplication.class, args);
        System.out.println("Hello World!");
    }
}
