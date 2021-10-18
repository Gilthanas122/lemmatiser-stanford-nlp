package com.example.stanfordnlpgerman.services.lemmaservice;

import com.example.stanfordnlpgerman.models.dtos.CreateNewsPaperArticleDTO;
import com.example.stanfordnlpgerman.repositories.LemmaRepository;
import org.springframework.stereotype.Service;

@Service
public class LemmaServiceImpl implements LemmaService {
  private final LemmaRepository lemmaRepository;

  public LemmaServiceImpl(LemmaRepository lemmaRepository) {
    this.lemmaRepository = lemmaRepository;
  }

  @Override
  public void saveArticle(CreateNewsPaperArticleDTO createNewsPaperArticleDTO) {

  }


  /*public static void main(String[] args) {
    String sampleGermanText = "Hallo, du, hi. Du solltest dich schämen.";
    Properties germanProperties = StringUtils.argsToProperties(
            new String[]{"-props", "StanfordCoreNLP-german.properties"});
    StanfordCoreNLP pipeline = new StanfordCoreNLP(germanProperties);

    CoreDocument coreDocument = new CoreDocument(sampleGermanText);

    pipeline.annotate(coreDocument);
    List<CoreLabel> coreLabels =  coreDocument.tokens();

    for (CoreLabel coreLabel: coreLabels) {
      if (!".,_?!:;-*'/+^/|&".contains(coreLabel.originalText())){
        GermanCoreLabel germanCoreLabel = new GermanCoreLabel(coreLabel);
        String lemma = germanCoreLabel.lemma();
        System.out.println(coreLabel.originalText() + " ~ " + lemma);
      }
    }
  }*/

}
