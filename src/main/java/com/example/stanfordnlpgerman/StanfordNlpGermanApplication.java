package com.example.stanfordnlpgerman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class StanfordNlpGermanApplication {

  public static void main(String[] args) {
    SpringApplication.run(StanfordNlpGermanApplication.class, args);
  }

}
