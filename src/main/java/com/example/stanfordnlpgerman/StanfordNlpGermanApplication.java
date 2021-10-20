package com.example.stanfordnlpgerman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableAsync
public class StanfordNlpGermanApplication {

  public static void main(String[] args) {
    SpringApplication.run(StanfordNlpGermanApplication.class, args);
  }

}
