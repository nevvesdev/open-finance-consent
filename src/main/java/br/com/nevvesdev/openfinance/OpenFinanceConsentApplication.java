package br.com.nevvesdev.openfinance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OpenFinanceConsentApplication {
    public static void main(String[] args) {
        SpringApplication.run(OpenFinanceConsentApplication.class, args);
    }
}