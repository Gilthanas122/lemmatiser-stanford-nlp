package com.example.stanfordnlpgerman.services.newsarticleservice;

import com.example.stanfordnlpgerman.component.SentenceCreationResources;
import com.example.stanfordnlpgerman.models.KeyWordsSingleton;
import com.example.stanfordnlpgerman.models.dao.*;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.CreateNewsPaperArticleDTO;
import com.example.stanfordnlpgerman.repositories.NewsArticleRepository;
import com.example.stanfordnlpgerman.services.lemmatypeservice.LemmaTypeService;
import com.example.stanfordnlpgerman.services.validations.ErrorServiceImpl;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class NewsArticleAsyncServiceImpl implements NewsArticleAsyncService {
  private final NewsArticleRepository newsArticleRepository;
  private final LemmaTypeService lemmaTypeService;
  private final StanfordCoreNLP pipeline;
  private final String PHRASAL_VERB = "PHRASVRB";
  private final String POSITION_IN_SENTENCE = "POSITION";
  private final String CORE_LABEL_POSITION = "CORE_LABEL_POSITION";
  private final String CURRENT_VERB_POSITION_IN_SENTENCE = "CURRENT_VERB_POSITION_IN_SENTENCE";

  public NewsArticleAsyncServiceImpl(NewsArticleRepository newsArticleRepository, LemmaTypeService lemmaTypeService, StanfordCoreNLP pipeline) {
    this.newsArticleRepository = newsArticleRepository;
    this.lemmaTypeService = lemmaTypeService;
    this.pipeline = pipeline;
  }

  @Async
  public void saveNewsPaperArticle(CreateNewsPaperArticleDTO createNewsPaperArticleDTO) {
    NewsArticle newsArticle = NewsArticle
        .builder()
        .newsPaperName(createNewsPaperArticleDTO.getNewsPaperName())
        .title(createNewsPaperArticleDTO.getTitle())
        .publicationYear(createNewsPaperArticleDTO.getPublicationYear())
        .build();
    newsArticle.setSentences(createSentencesFromNewsPaperArticle(createNewsPaperArticleDTO.getText(), newsArticle));
    Set<LemmaType> lemmaTypes = createLemmaTypesFromSentencesForNewsArticle(newsArticle.getSentences());
    newsArticle.setLemmaTypes(lemmaTypes);
    newsArticle.setRelevance(setRelevanceForNewsArticleByLemmaTypes(lemmaTypes));
    log.info("NewsArticle with title {} and number of sentences {} saved", newsArticle.getTitle(), newsArticle.getSentences().size());
    newsArticleRepository.save(newsArticle);
  }

  private int setRelevanceForNewsArticleByLemmaTypes(Set<LemmaType> lemmaTypes) {
    final int[] relevance = {0};
    Set<String> keyWords = KeyWordsSingleton.getKeyWords();
    lemmaTypes.forEach(lemmaType -> {
      if (StringUtils.isBlank(lemmaType.getText()) && keyWords.contains(lemmaType.getText())) {
        relevance[0] += 1;
      }
    });
    return relevance[0];
  }

  private Set<LemmaType> createLemmaTypesFromSentencesForNewsArticle(List<Sentence> sentences) {
    return sentences
        .stream()
        .map(Sentence::getLemmaTypes)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }

  private List<Sentence> createSentencesFromNewsPaperArticle(String text, NewsArticle newsArticle) {
    CoreDocument coreDocument = new CoreDocument(text);
    pipeline.annotate(coreDocument);
    List<CoreSentence> coreSentences = coreDocument.sentences();

    short[] sentencePosition = {0};
    List<Sentence> sentences = new ArrayList<>();
    coreSentences.forEach(coreSentence -> {
      Sentence sentence = Sentence
          .builder()
          .newsArticle(newsArticle)
          .text(coreSentence.text())
          .textPosition(sentencePosition[0])
          .build();
      if (ErrorServiceImpl.invalidSentence(sentence.getText())) {
        sentence.setInvalid(true);
      }
      Pair<Set<LemmaType>, List<TextToken>> lemmaTypeAndTextTokens = createLemmaTypesFromSentences(coreSentence, sentence, newsArticle);
      sentence.setLemmaTypes(lemmaTypeAndTextTokens.getLeft());
      sentence.setTextTokens(new ArrayList<>(lemmaTypeAndTextTokens.getRight()));
      sentencePosition[0]++;
      sentences.add(sentence);
    });
    return sentences;
  }

  @Transactional
  public Pair<Set<LemmaType>, List<TextToken>> createLemmaTypesFromSentences(CoreSentence coreSentence, Sentence sentence, NewsArticle newsArticle) {
    List<CoreLabel> coreLabels = coreSentence.tokens();
    Map<String, Integer> valuesByPosition = new HashMap<>(Map.of(
        POSITION_IN_SENTENCE, 0, CORE_LABEL_POSITION, 0, CURRENT_VERB_POSITION_IN_SENTENCE, 0
    ));
    List<LemmaType> currentVerbLemmaTypeInSentence = Arrays.asList(new LemmaType());

    List<String> wordsInSentence = coreSentence.tokensAsStrings();
    List<TextToken> textTokens = new ArrayList<>();
    List<LemmaType> lemmaTypes = new ArrayList<>();

    try {
      wordsInSentence
          .forEach(word -> {
                log.info("Word being processed {}", word);
                LemmaType lemmaType = null;
                String phraseType = null;
                if (SentenceCreationResources.isSentenceSign(word)) {
                  String phrasal = wordsInSentence.get(wordsInSentence.indexOf(word) - 1);
                  String verbText = currentVerbLemmaTypeInSentence.get(0).getText();
                  if (SentenceCreationResources.isPhrasal(phrasal) && !verbText.isEmpty()) {
                    String verb = phrasal + verbText;
                    phraseType = PHRASAL_VERB;
                    lemmaType = createLemmaTypeFromTextToken(verb, valuesByPosition.get(CURRENT_VERB_POSITION_IN_SENTENCE), sentence, phraseType, newsArticle, currentVerbLemmaTypeInSentence,textTokens);
                  }
                } else {
                  phraseType = coreLabels.get(valuesByPosition.get(CORE_LABEL_POSITION)).get(CoreAnnotations.PartOfSpeechAnnotation.class);
                  lemmaType = createLemmaTypeFromTextToken(word, valuesByPosition.get(CURRENT_VERB_POSITION_IN_SENTENCE), sentence, phraseType, newsArticle, currentVerbLemmaTypeInSentence,textTokens);
                  valuesByPosition.put(POSITION_IN_SENTENCE, valuesByPosition.get(POSITION_IN_SENTENCE) +1);
                  valuesByPosition.put(CORE_LABEL_POSITION, valuesByPosition.get(CORE_LABEL_POSITION) +1);
                }
                if (lemmaType != null && !phraseType.equals(PHRASAL_VERB)) {
                  lemmaTypes.add(lemmaType);
                } else {
                  lemmaTypes.set(valuesByPosition.get(CURRENT_VERB_POSITION_IN_SENTENCE), lemmaType);
                }
              }
          );
    } catch (Exception e){
      log.error("Exception occurred during processing a sentence {}", e.getMessage());
    }
    return Pair.of(new HashSet<>(lemmaTypes), textTokens);
  }

  private LemmaType createLemmaTypeFromTextToken(String word, int position, Sentence sentence, String phraseType, NewsArticle newsArticle, List<LemmaType> currentVerbLemmaTypeInSentence, List<TextToken> textTokens) {
    LemmaType lemmaType = new LemmaType();
    Set<LemmaType> lemmaTypesFromDatabase = lemmaTypeService.findAllByText(word);
    TextToken textToken = TextToken
        .builder()
        .text(word)
        .sentencePosition((short) position)
        .sentence(sentence)
        .phraseType(phraseType)
        .build();
    if (phraseType.equals(PHRASAL_VERB)){
      textTokens.set(position, textToken);
    } else {
      textTokens.add(textToken);
    }
    lemmaType = getLemmaTypeFromSet(lemmaTypesFromDatabase);
    lemmaType.addOneTextToken(textToken);
    lemmaType.addOneSentence(sentence);
    lemmaType.addOneNewsArticle(newsArticle);
    if (lemmaTypesFromDatabase.isEmpty()){
      lemmaType.setInvalid(true);
    }

    textToken.setLemmaType(lemmaType);
    if (lemmaTypesFromDatabase.size() != 1) {
      textToken.setInvalid(true);
    }
    if (phraseType.equals("VERB")){
      currentVerbLemmaTypeInSentence.set(0, lemmaType);
    }
    return lemmaType;
  }

  private LemmaType getLemmaTypeFromSet(Set<LemmaType> lemmaTypesFromDatabase) {
    return lemmaTypesFromDatabase.stream()
        .reduce((first, second) -> first)
        .orElse(new LemmaType());
  }
}
