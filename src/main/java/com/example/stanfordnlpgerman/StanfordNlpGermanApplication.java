package com.example.stanfordnlpgerman;

import com.example.stanfordnlpgerman.models.dao.LemmaToken;
import com.example.stanfordnlpgerman.models.dao.LemmaType;
import com.example.stanfordnlpgerman.repositories.LemmaTypeRepository;
import com.example.stanfordnlpgerman.services.lemmatypeservice.GermanCoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.*;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableAsync
public class StanfordNlpGermanApplication /*implements CommandLineRunner*/  {
/*  @Autowired
  private LemmaTypeRepository lemmaTypeRepository;
  @Autowired
  private StanfordCoreNLP pipeline;*/

  public static void main(String[] args) {
    SpringApplication.run(StanfordNlpGermanApplication.class, args);
  }

  /*@Override
  public void run(String... args) throws Exception {
    List<String> fileContent = GermanCoreLabel.getFileContent();
    TreeMap<String, TreeSet<String>> typeTokens = splitFileContent(fileContent);
    Set<LemmaType> lemmaTypes = new TreeSet<>();
    for (Map.Entry<String, TreeSet<String>> entry : typeTokens.entrySet()) {
      List<LemmaToken> lemmaTokens = new ArrayList<>();
      LemmaType lemmaType = LemmaType
              .builder()
              .text(entry.getKey())
              .build();
      for (String value : entry.getValue()) {
        CoreDocument coreDocument = new CoreDocument(value);
        pipeline.annotate(coreDocument);
        List<CoreLabel> coreLabels = coreDocument.tokens();
        String phraseType = coreLabels.get(0).get(CoreAnnotations.PartOfSpeechAnnotation.class);
        LemmaToken lemmaToken = LemmaToken
                .builder()
                .text(value)
                .lemmaType(lemmaType)
                .phraseType(phraseType)
                .build();
        lemmaTokens.add(lemmaToken);
      }
      lemmaType.setLemmaTokens(lemmaTokens);
      lemmaTypes.add(lemmaType);
    }
    lemmaTypeRepository.saveAll(lemmaTypes);
  }*/

/*  private static TreeMap<String, TreeSet<String>> splitFileContent(List<String> fileContent) {
    TreeMap<String, TreeSet<String>> typeToken = new TreeMap<>();
    for (int i = 0; i < fileContent.size(); i++) {
      String[] split = fileContent.get(i).split("\\s+");
      if (typeToken.containsKey(split[0])) {
        TreeSet<String> tokens = new TreeSet<>(typeToken.get(split[0]));
        tokens.add(split[1]);
        typeToken.put(split[0], tokens);
      } else {
        TreeSet<String> tokens = new TreeSet<>(Collections.singleton(split[1]));
        typeToken.put(split[0], tokens);
      }
    }
    return typeToken;
  }*/
}
