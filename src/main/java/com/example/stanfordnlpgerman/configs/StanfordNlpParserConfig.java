package com.example.stanfordnlpgerman.configs;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Properties;

@Configuration
public class StanfordNlpParserConfig {

  @Bean
  @Primary
  StanfordCoreNLP getStanfordCoreNlp() {
    Properties germanProperties = StringUtils.argsToProperties(
            "-props", "StanfordCoreNLP-german.properties");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(germanProperties);
    return pipeline;
  }
}
