package com.example.stanfordnlpgerman.services.newsarticleservice;

import com.example.stanfordnlpgerman.exceptions.validations.MissingParamsException;
import com.example.stanfordnlpgerman.models.dtos.CreateNewsPaperArticleDTO;
import com.example.stanfordnlpgerman.services.validations.ErrorServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class NewsArticleServiceImpl implements NewsArticleService {
  private final NewsArticleAsyncService newsArticleAsyncService;

  public NewsArticleServiceImpl(NewsArticleAsyncService newsArticleAsyncService) {
    this.newsArticleAsyncService = newsArticleAsyncService;
  }

  @Override
  public void saveArticle(CreateNewsPaperArticleDTO createNewsPaperArticleDTO) throws MissingParamsException {
    ErrorServiceImpl.buildMissingFieldErrorMessage(createNewsPaperArticleDTO);
    if (createNewsPaperArticleDTO.getPageNumber() < 1) {
      throw new MissingParamsException("Following parameters are missing: pageNumber");
    }
    newsArticleAsyncService.createNewsPaperArticle(createNewsPaperArticleDTO);
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
