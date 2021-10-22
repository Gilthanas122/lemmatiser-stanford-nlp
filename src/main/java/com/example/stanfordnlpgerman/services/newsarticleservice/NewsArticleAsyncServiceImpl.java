package com.example.stanfordnlpgerman.services.newsarticleservice;

import com.example.stanfordnlpgerman.models.dao.*;
import com.example.stanfordnlpgerman.models.dtos.CreateNewsPaperArticleDTO;
import com.example.stanfordnlpgerman.repositories.NewsArticleRepository;
import com.example.stanfordnlpgerman.services.lemmatypeservice.LemmaTypeService;
import com.example.stanfordnlpgerman.services.texttokenservice.TextTokenService;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class NewsArticleAsyncServiceImpl implements NewsArticleAsyncService {
  private List<TextToken> filteredTextTokens = new ArrayList<>();
  private final NewsArticleRepository newsArticleRepository;
  private final LemmaTypeService lemmaTypeService;
  private final StanfordCoreNLP pipeline;
  private final TextTokenService textTokenService;

  public NewsArticleAsyncServiceImpl(NewsArticleRepository newsArticleRepository, LemmaTypeService lemmaTypeService, StanfordCoreNLP pipeline, TextTokenService textTokenService) {
    this.newsArticleRepository = newsArticleRepository;
    this.lemmaTypeService = lemmaTypeService;
    this.pipeline = pipeline;
    this.textTokenService = textTokenService;
  }

  @Async
  public void createNewsPaperArticle(CreateNewsPaperArticleDTO createNewsPaperArticleDTO) {
    NewsArticle newsArticle = NewsArticle
            .builder()
            .newsPaperName(createNewsPaperArticleDTO.getNewsPaperName())
            .title(createNewsPaperArticleDTO.getTitle())
            .pageNumber(createNewsPaperArticleDTO.getPageNumber())
            .author(createNewsPaperArticleDTO.getAuthor())
            .publicationDate(createNewsPaperArticleDTO.getPublicationDate())
            .build();
    newsArticle.setSentences(createSentencesFromNewsPaperArticle(createNewsPaperArticleDTO.getText(), newsArticle));
    newsArticle.setLemmaTypes(createLemmaTypesFromSentencesForNewsArticle(newsArticle.getSentences()));
    newsArticle.setTextTokens(createTextTokensFromSentencesForNewsArticle(newsArticle.getSentences()));
    newsArticleRepository.save(newsArticle);
  }

  private List<LemmaType> createLemmaTypesFromSentencesForNewsArticle(List<Sentence> sentences) {
    List<LemmaType> lemmaTypes = new ArrayList<>();
    for (Sentence sentence : sentences) {
      lemmaTypes.addAll(sentence.getLemmaTypes());
    }
    return lemmaTypes;
  }

  private List<TextToken> createTextTokensFromSentencesForNewsArticle(List<Sentence> sentences) {
    List<TextToken> textTokens = new ArrayList<>();
    for (Sentence sentence : sentences) {
      textTokens.addAll(sentence.getTextTokens());
    }
    return textTokens;
  }

  private List<Sentence> createSentencesFromNewsPaperArticle(String text, NewsArticle newsArticle) {
    CoreDocument coreDocument = new CoreDocument(text);
    pipeline.annotate(coreDocument);
    List<CoreSentence> coreSentences = coreDocument.sentences();
    List<Sentence> sentences = new ArrayList<>();
    int position = 0;
    for (CoreSentence sent : coreSentences) {
      Sentence sentence = Sentence
              .builder()
              .newsArticle(newsArticle)
              .text(sent.text())
              .textPosition(position)
              .build();
      sentence.setLemmaTypes(createLemmaTypesFromSentences(sent, sentence, newsArticle, coreDocument));
      sentence.setTextTokens(filteredTextTokens);
      sentences.add(sentence);
      position++;
    }
    return sentences;
  }

  private List<TextToken> createTextTokensFromSentences(List<LemmaType> lemmaTypes) {
    List<TextToken> textTokens = new ArrayList<>();
    for (LemmaType lemmaType : lemmaTypes) {
      textTokens.addAll(lemmaType.getTextTokens());
    }
    return textTokens;
  }

  @Transactional
  protected List<LemmaType> createLemmaTypesFromSentences(CoreSentence coreSentence, Sentence sentence, NewsArticle newsArticle, CoreDocument coreDocument) {
    List<CoreLabel> coreLabels = coreSentence.tokens();
    List<LemmaType> lemmaTypes = new ArrayList<>();
    int position = 0;
    int coreLabelPosition = 0;
    for (String word : coreSentence.tokensAsStrings()) {
      if (word.matches("^[a-zA-Z0-9\u00C0-\u00FF]*$")) {
        String phraseType = coreLabels.get(coreLabelPosition).get(CoreAnnotations.PartOfSpeechAnnotation.class);
        Set<LemmaType> lemmaTypesReturned = lemmaTypeService.findByText(word);
        TextToken textToken = TextToken
                .builder()
                .text(word)
                .sentencePosition(position)
                .newsArticle(newsArticle)
                .sentence(sentence)
                .phraseType(phraseType)
                .build();
        filteredTextTokens.add(textToken);
        if (lemmaTypesReturned.size() > 0) {
          LemmaType lemmaType = getLemmaTypeFromSet(word, lemmaTypesReturned);
          lemmaType.addOneTextToken(textToken);
          lemmaType.addOneSentence(sentence);
          lemmaType.addOneNewsArticle(newsArticle);
          textToken.setLemmaType(lemmaType);
          lemmaTypes.add(lemmaType);
        }
        if (lemmaTypesReturned.size() != 1) {
          textToken.setInvalid(true);
          if (lemmaTypesReturned.size() < 1){
            textTokenService.saveTextTokenWithoutLemmaType(textToken);
          }
        }
        position++;
      }
      coreLabelPosition++;
    }
    sentence.setLemmaTypes(lemmaTypes);
    return lemmaTypes;
  }

  private LemmaType getLemmaTypeFromSet(String word, Set<LemmaType> lemmaTypesReturned) {
    for (LemmaType lemmaType : lemmaTypesReturned ) {
      if (lemmaType.getText().equalsIgnoreCase(word)) {
        return lemmaType;
      } else {
        return lemmaType.getLemmaTokens()
                .stream()
                .filter(lemmaToken -> lemmaToken.getText().equals(word))
                .findFirst()
                .map(LemmaToken::getLemmaType)
                .orElse(null);
      }
    }
    return null;
  }
}
