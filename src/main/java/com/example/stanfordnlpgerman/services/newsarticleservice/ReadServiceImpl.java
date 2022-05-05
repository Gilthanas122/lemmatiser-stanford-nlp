package com.example.stanfordnlpgerman.services.newsarticleservice;

import com.example.stanfordnlpgerman.models.KeyWordsSingleton;
import com.example.stanfordnlpgerman.models.dao.*;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.CreateNewsPaperArticleDTO;
import com.example.stanfordnlpgerman.repositories.LemmaTokenRepository;
import com.example.stanfordnlpgerman.repositories.LemmaTypeRepository;
import com.example.stanfordnlpgerman.repositories.NewsArticleRepository;
import com.example.stanfordnlpgerman.services.texttokenservice.TextTokenService;
import com.example.stanfordnlpgerman.services.validations.ErrorServiceImpl;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReadServiceImpl implements ReadService {
  private final LemmaTypeRepository lemmaTypeRepository;
  private final LemmaTokenRepository lemmaTokenRepository;
  private final NewsArticleRepository newsArticleRepository;
  private final List<TextToken> filteredTextTokens = new ArrayList<>();
  private final Set<TextToken> invalidTextTokens = new HashSet<>();
  private final StanfordCoreNLP pipeline;
  private final TextTokenService textTokenService;
  private List<LemmaType> allLemmaTypes = new ArrayList<>();
  private List<LemmaToken> allLemmaTokens = new ArrayList<>();

  public ReadServiceImpl(LemmaTypeRepository lemmaTypeRepository, LemmaTokenRepository lemmaTokenRepository, NewsArticleRepository newsArticleRepository, StanfordCoreNLP pipeline, TextTokenService textTokenService) {
    this.lemmaTypeRepository = lemmaTypeRepository;
    this.lemmaTokenRepository = lemmaTokenRepository;
    this.newsArticleRepository = newsArticleRepository;
    this.pipeline = pipeline;
    this.textTokenService = textTokenService;
  }

  @Override
  public void startReading() throws Exception {
    File folder = new File("articles");
    allLemmaTokens = lemmaTokenRepository.findAll();
    allLemmaTypes = lemmaTypeRepository.findAll();

    CreateNewsPaperArticleDTO createNewsPaperArticleDTO = new CreateNewsPaperArticleDTO();
    for (File file: folder.listFiles()) {
      List<String> lines = Files.readAllLines(Paths.get(file.getPath()));
      createNewsPaperArticleDTO.setNewsPaperName(lines.get(0));
      createNewsPaperArticleDTO.setPublicationYear(Integer.parseInt(lines.get(1).trim()));
      createNewsPaperArticleDTO.setTitle(lines.get(2));
      StringBuilder temp = new StringBuilder();
      for (int i = 3; i <lines.size(); i++) {
        if (lines.get(i) != null && !lines.get(i).isEmpty()){
          temp.append(lines.get(i));
        }
      }
      createNewsPaperArticleDTO.setText(temp.toString());
    }

    createNewsPaperArticle(createNewsPaperArticleDTO);
  }

  public void createNewsPaperArticle(CreateNewsPaperArticleDTO createNewsPaperArticleDTO) throws Exception {
    long startTime = System.currentTimeMillis();
    NewsArticle newsArticle = NewsArticle
            .builder()
            .newsPaperName(createNewsPaperArticleDTO.getNewsPaperName())
            .title(createNewsPaperArticleDTO.getTitle())
            .publicationYear(createNewsPaperArticleDTO.getPublicationYear())
            .build();
    newsArticle.setSentences(createSentencesFromNewsPaperArticle(createNewsPaperArticleDTO.getText(), newsArticle));
    List<LemmaType> lemmaTypes = createLemmaTypesFromSentencesForNewsArticle(newsArticle.getSentences());
    newsArticle.setLemmaTypes(lemmaTypes);
    newsArticle.setRelevance(setRelevanceForNewsArticleByLemmaTypes(lemmaTypes));
    newsArticleRepository.save(newsArticle);
    textTokenService.saveAllInvalidTextTokens(invalidTextTokens);
    long endTime = System.currentTimeMillis();
    System.out.println("Time elapsed " + (endTime - startTime));
  }

  private int setRelevanceForNewsArticleByLemmaTypes(List<LemmaType> lemmaTypes) {
    int relevance = 0;
    Set<String> keyWords = KeyWordsSingleton.getKeyWords();
    for (String keyword : keyWords) {
      for (LemmaType lemmaType : lemmaTypes) {
        if (lemmaType.getText().equals(keyword)){
          relevance++;
        }
      }
    }
    return relevance;
  }

  private List<LemmaType> createLemmaTypesFromSentencesForNewsArticle(List<Sentence> sentences) {
    List<LemmaType> lemmaTypes = new ArrayList<>();
    for (Sentence sentence : sentences) {
      lemmaTypes.addAll(sentence.getLemmaTypes());
    }
    return lemmaTypes;
  }

  private List<Sentence> createSentencesFromNewsPaperArticle(String text, NewsArticle newsArticle) throws Exception {
    CoreDocument coreDocument = new CoreDocument(text);
    pipeline.annotate(coreDocument);
    List<CoreSentence> coreSentences = coreDocument.sentences();
    List<Sentence> sentences = new ArrayList<>();
    short position = 0;
    for (CoreSentence sent : coreSentences) {
      Sentence sentence = Sentence
              .builder()
              .newsArticle(newsArticle)
              .text(sent.text())
              .textPosition(position)
              .build();
      if (ErrorServiceImpl.invalidSentence(sentence.getText())) {
        sentence.setInvalid(true);
      }
      sentence.setLemmaTypes(createLemmaTypesFromSentences(sent, sentence, newsArticle));
      sentence.setTextTokens(filteredTextTokens);
      filteredTextTokens.clear();
      sentences.add(sentence);
      position++;
    }
    return sentences;
  }

  @Transactional
  protected List<LemmaType> createLemmaTypesFromSentences(CoreSentence coreSentence, Sentence sentence, NewsArticle newsArticle) {
    List<CoreLabel> coreLabels = coreSentence.tokens();
    List<LemmaType> lemmaTypes = new ArrayList<>();

    short position = 0;
    short coreLabelPosition = 0;

    for (String word : coreSentence.tokensAsStrings()) {
      word = word.replaceAll("[^0-9\\p{L}\\s]", "");
      if (!word.isEmpty()) {
        String phraseType = coreLabels.get(coreLabelPosition).get(CoreAnnotations.PartOfSpeechAnnotation.class);
        Set<LemmaType> lemmaTypesReturned = findByText(word);
        TextToken textToken = TextToken
                .builder()
                .text(word)
                .sentencePosition(position)
                .sentence(sentence)
                .phraseType(phraseType)
                .build();
        filteredTextTokens.add(textToken);
        if (lemmaTypesReturned.size() == 1) {
          LemmaType lemmaType = getLemmaTypeFromSet(word, lemmaTypesReturned);
          lemmaType.addOneTextToken(textToken);
          lemmaType.addOneSentence(sentence);
          lemmaType.addOneNewsArticle(newsArticle);
          textToken.setLemmaType(lemmaType);
          lemmaTypes.add(lemmaType);
        } else {
          textToken.setInvalid(true);
          invalidTextTokens.add(textToken);
        }
        position++;
      }
      coreLabelPosition++;
    }
    return lemmaTypes;
  }

  private Set<LemmaType> findByText(String word) {
    Set<LemmaType> lemmaTypesFound = new HashSet<>();
    for (int i = 0; i <allLemmaTokens.size(); i++) {
      if (allLemmaTokens.get(i).getText().equalsIgnoreCase(word)){
        lemmaTypesFound.add(allLemmaTokens.get(i).getLemmaType());
      }
    }
    for (int i = 0; i <allLemmaTypes.size(); i++) {
      if (allLemmaTypes.get(i).getText().equalsIgnoreCase(word)){
        lemmaTypesFound.add(allLemmaTypes.get(i));
      }
    }
    return lemmaTypesFound;
  }

  private LemmaType getLemmaTypeFromSet(String word, Set<LemmaType> lemmaTypesReturned) {
    LemmaType toReturn = null;
    for (LemmaType lemmaType : lemmaTypesReturned) {
      if (lemmaType.getText().equalsIgnoreCase(word)) {
        toReturn = lemmaType;
      } else {
        toReturn = lemmaType.getLemmaTokens()
                .stream()
                .filter(lemmaToken -> lemmaToken.getText().equalsIgnoreCase(word))
                .findFirst()
                .map(LemmaToken::getLemmaType)
                .orElse(null);
      }
    }
    if (toReturn == null) {
      LemmaToken lemmaTokenToGetPhraseType = new LemmaToken();
      for (LemmaType lt : lemmaTypesReturned) {
        toReturn = lt;
        lemmaTokenToGetPhraseType = (LemmaToken) lt.getLemmaTokens().stream().toArray()[0];
      }
      toReturn.addOneLemmaToken(LemmaToken.builder().text(word).lemmaType(toReturn).phraseType(lemmaTokenToGetPhraseType.getPhraseType()).build());
    }
    return toReturn;
  }
}