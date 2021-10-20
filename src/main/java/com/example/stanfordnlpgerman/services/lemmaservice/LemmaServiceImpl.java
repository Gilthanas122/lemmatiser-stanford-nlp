package com.example.stanfordnlpgerman.services.lemmaservice;

import com.example.stanfordnlpgerman.exceptions.validations.MissingParamsException;
import com.example.stanfordnlpgerman.models.dao.Lemma;
import com.example.stanfordnlpgerman.models.dao.NewsArticle;
import com.example.stanfordnlpgerman.models.dao.Sentence;
import com.example.stanfordnlpgerman.models.dtos.CreateNewsPaperArticleDTO;
import com.example.stanfordnlpgerman.repositories.NewsArticleRepository;
import com.example.stanfordnlpgerman.services.validations.ErrorServiceImpl;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LemmaServiceImpl implements LemmaService {
  private final NewsArticleRepository newsArticleRepository;
  private final StanfordCoreNLP pipeline;

  public LemmaServiceImpl(NewsArticleRepository newsArticleRepository, StanfordCoreNLP pipeline) {
    this.newsArticleRepository = newsArticleRepository;
    this.pipeline = pipeline;
  }


  @Override
  public void saveArticle(CreateNewsPaperArticleDTO createNewsPaperArticleDTO) throws MissingParamsException {
    ErrorServiceImpl.buildMissingFieldErrorMessage(createNewsPaperArticleDTO);
    if (createNewsPaperArticleDTO.getPageNumber() < 1){
      throw new MissingParamsException("Following parameters are missing: pageNumber");
    }
    createNewsPaperArticle(createNewsPaperArticleDTO);
  }

  @Async
  protected void createNewsPaperArticle(CreateNewsPaperArticleDTO createNewsPaperArticleDTO){
    NewsArticle newsArticle = NewsArticle
            .builder()
            .newsPaperName(createNewsPaperArticleDTO.getNewsPaperName())
            .title(createNewsPaperArticleDTO.getTitle())
            .pageNumber(createNewsPaperArticleDTO.getPageNumber())
            .author(createNewsPaperArticleDTO.getAuthor())
            .publicationDate(createNewsPaperArticleDTO.getPublicationDate())
            .build();
    newsArticle.setSentences(createSentencesFromNewsPaperArticle(createNewsPaperArticleDTO.getText(), newsArticle));
    newsArticleRepository.save(newsArticle);
  }
  private List<Sentence> createSentencesFromNewsPaperArticle(String text, NewsArticle newsArticle) {
    CoreDocument coreDocument = new CoreDocument(text);
    pipeline.annotate(coreDocument);
    List<CoreSentence> coreSentences =  coreDocument.sentences();

    List<String> stringSentences =  coreSentences
            .stream()
            .map(coreSentence -> coreSentence.toString())
            .collect(Collectors.toList());
    List<Sentence> sentences = new ArrayList<>();
    int position = 0;
    for (String sent: stringSentences) {
      Sentence sentence = Sentence
              .builder()
              .newsArticle(newsArticle)
              .text(sent)
              .textPosition(position)
              .build();
      sentence.setLemmas(createLemmasFromSentences(coreDocument, sentence, newsArticle));

      sentences.add(sentence);
      position++;
    }
    return sentences;
  }

  private List<Lemma> createLemmasFromSentences(CoreDocument coreDocument, Sentence sentence, NewsArticle newsArticle) {
    List<CoreLabel> coreLabels =  coreDocument.tokens();
    List <Lemma> lemmas= new ArrayList<>();

    for (CoreLabel coreLabel: coreLabels) {
      if (!".,_?!:;-*'/+^/|&".contains(coreLabel.originalText())){
        GermanCoreLabel germanCoreLabel = new GermanCoreLabel(coreLabel);
        String stem = germanCoreLabel.lemma();
        String phraseType = coreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class);
        Lemma lemma = new Lemma();
        lemma.setText(stem);
        lemma.setPhraseType(phraseType);
        lemma.addOneSentence(sentence);
        lemma.addOneNewsArticle(newsArticle);
        lemmas.add(lemma);
      }
    }
    return lemmas;
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
