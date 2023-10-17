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
    lemmaTypes.forEach(lemmaType -> {
      if (StringUtils.isBlank(lemmaType.getText()) && KeyWordsSingleton.isKeyWord(lemmaType.getText())) {
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
        POSITION_IN_SENTENCE, 0, CURRENT_VERB_POSITION_IN_SENTENCE, 0, CORE_LABEL_POSITION, 0
    ));
    // POSITION_IN_SENTENCE track the order of verbs in sentence
    // CURRENT_VERB_POSITION_IN_SENTENCE tracking the position of previous verb in sentence
    // CORE_LABEL_POSITION track core label position for creating phraseType
    List<LemmaType> currentVerbLemmaTypeInSentence = Arrays.asList(new LemmaType());

    List<String> wordsInSentence = coreSentence.tokensAsStrings();
    List<TextToken> textTokens = new ArrayList<>();
    List<LemmaType> lemmaTypes = new ArrayList<>();
    final boolean [] verbSwitched = {false};

    try {
      wordsInSentence
          .forEach(word -> {
                log.info("Word being processed {}", word);
                LemmaType lemmaType = new LemmaType();
                String phraseType = "";

                if (SentenceCreationResources.isSentenceSign(word)) {
                  String phrasal = wordsInSentence.get(wordsInSentence.indexOf(word) - 1);
                  String verbText = currentVerbLemmaTypeInSentence.get(0).getText();
                  if (SentenceCreationResources.isPhrasal(phrasal) && !verbText.isEmpty()) {
                    int positionToSwitch = verbSwitched[0] ? valuesByPosition.get(POSITION_IN_SENTENCE) : valuesByPosition.get(CURRENT_VERB_POSITION_IN_SENTENCE); // in case verb has been already switched we need to use the sentence position
                    int positionTextToken = valuesByPosition.get(CURRENT_VERB_POSITION_IN_SENTENCE);
                    verbSwitched[0] = true;
                    String verb = phrasal + verbText;
                    phraseType = PHRASAL_VERB;
                    lemmaType = createLemmaTypeFromTextToken(verb, phrasal, positionToSwitch, positionTextToken,
                        sentence, phraseType, newsArticle, textTokens, currentVerbLemmaTypeInSentence.get(0));
                    handlePreviouslyPhrasal(textTokens, positionToSwitch);
                    valuesByPosition.put(POSITION_IN_SENTENCE, valuesByPosition.get(POSITION_IN_SENTENCE) - 1); // we need to set it back so that ADP doesn't count as part of the sentence
                  }
                }
                if (!SentenceCreationResources.isSentenceSign(word) || List.of("und", "oder").contains(word)){
                  // second part of if condition: in German with conjunctive sentences there is no comma before und and oder but should be processed as words
                  // since in the previous if branch I set it to PHRASVERB it needs to be redone
                  phraseType = coreLabels.get(valuesByPosition.get(CORE_LABEL_POSITION)).get(CoreAnnotations.PartOfSpeechAnnotation.class);
                  if (phraseType.equals("VERB")) { // update it so that we can do the logic for phrasal verbs
                    valuesByPosition.put(CURRENT_VERB_POSITION_IN_SENTENCE, valuesByPosition.get(POSITION_IN_SENTENCE));
                  }
                  lemmaType = createLemmaTypeFromTextToken(word, null, valuesByPosition.get(CURRENT_VERB_POSITION_IN_SENTENCE),
                      valuesByPosition.get(POSITION_IN_SENTENCE), sentence, phraseType, newsArticle, textTokens, null);
                  valuesByPosition.put(POSITION_IN_SENTENCE, valuesByPosition.get(POSITION_IN_SENTENCE) + 1);
                  valuesByPosition.put(CORE_LABEL_POSITION, valuesByPosition.get(CORE_LABEL_POSITION) + 1);

                }
                if (phraseType.equals("VERB")){
                  currentVerbLemmaTypeInSentence.set(0, lemmaType);
                }
                if (lemmaType.getId() > 0 && !phraseType.equals(PHRASAL_VERB)) { // in case there was no match in DB we wish to store the given lemmaType
                  lemmaTypes.add(lemmaType);
                } else if (phraseType.equals(PHRASAL_VERB)) { // in case it is a phrasal verb, we need to update previously stored lemmaType
                  lemmaTypes.set(valuesByPosition.get(CURRENT_VERB_POSITION_IN_SENTENCE), lemmaType);
                }
              }
          );
    } catch (Exception e) {
      log.error("Exception occurred during processing a sentence {}", e.getMessage());
    }
    return Pair.of(new HashSet<>(lemmaTypes), textTokens);
  }

  private void handlePreviouslyPhrasal(List<TextToken> textTokens,int positionToUpdate) {
    updateTextTokenForPhrasal(textTokens.size() -2, textTokens); // for phrasal
    updateTextTokenForPhrasal(positionToUpdate, textTokens); // for previous verb
  }

  private void updateTextTokenForPhrasal(Integer positionToUpdate, List<TextToken> textTokens) {
    TextToken previouslyPhrasal = textTokens.get(positionToUpdate);
    previouslyPhrasal.setDeleted(true); // so that it doesn't occur in searches
    previouslyPhrasal.setRemovedPhrasal(true); // later we can set back easier the original state
  }

  private LemmaType createLemmaTypeFromTextToken(String word, String phrasal, int verbPosition, int positionInSentence, Sentence sentence, String phraseType, NewsArticle newsArticle, List<TextToken> textTokens, LemmaType originalLemmaType) {
    String textTokenText = createTextTokenText(word, phraseType, phrasal, textTokens, verbPosition); ///create textToken but keep the previous one see next comment
    Set<LemmaType> lemmaTypesFromDatabase = lemmaTypeService.findAllByText(word);

    TextToken textToken = TextToken
        .builder()
        .text(textTokenText)
        .sentencePosition((short) positionInSentence)
        .sentence(sentence)
        .phraseType(phraseType)
        .build();

    textTokens.add(textToken);
    LemmaType lemmaType = getLemmaTypeFromSet(lemmaTypesFromDatabase);
    lemmaType.addOneTextToken(textToken);
    lemmaType.addOneSentence(sentence);
    lemmaType.addOneNewsArticle(newsArticle);
    if (originalLemmaType != null){
      lemmaType.setReferenceLemmaTypeId(originalLemmaType.getId());
    }

    if (lemmaTypesFromDatabase.isEmpty() && phraseType.equals(PHRASAL_VERB)) {
      lemmaType.setInvalid(true); // set to invalid if phrasalform doesn't exist in db
      lemmaType.setText(word);
    }

    textToken.setLemmaType(lemmaType);
    if ((lemmaTypesFromDatabase.isEmpty() && phraseType.equals(PHRASAL_VERB)) || (lemmaTypesFromDatabase.size() != 1 && !phraseType.equals(PHRASAL_VERB))) {
      textToken.setInvalid(true);
    }

    return lemmaType;
  }

  private String createTextTokenText(String word, String phraseType, String phrasal, List<TextToken> textTokens, int position) {
    return phraseType.equals(PHRASAL_VERB) ? phrasal + textTokens.get(position).getText() : word;
  }

  private LemmaType getLemmaTypeFromSet(Set<LemmaType> lemmaTypesFromDatabase) {
    return lemmaTypesFromDatabase.stream()
        .reduce((first, second) -> first)
        .orElse(new LemmaType());
  }
}
