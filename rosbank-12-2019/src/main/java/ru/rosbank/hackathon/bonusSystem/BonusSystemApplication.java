package ru.rosbank.hackathon.bonusSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationPropertiesScan("ru.rosbank.hackathon.bonusSystem")
@EnableScheduling
public class BonusSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BonusSystemApplication.class, args);
    }
}
