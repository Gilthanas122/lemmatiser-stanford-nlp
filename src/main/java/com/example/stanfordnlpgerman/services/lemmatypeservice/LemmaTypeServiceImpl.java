package com.example.stanfordnlpgerman.services.lemmatypeservice;

import com.example.stanfordnlpgerman.models.KeyWordsSingleton;
import com.example.stanfordnlpgerman.models.dao.*;
import com.example.stanfordnlpgerman.models.dtos.lemmatype.ShowMostCommonLemmasDTO;
import com.example.stanfordnlpgerman.models.dtos.sentence.LemmaOccurenceInSentencesDTO;
import com.example.stanfordnlpgerman.repositories.LemmaTypeRepository;
import com.example.stanfordnlpgerman.repositories.TextTokenRepository;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@Transactional
@Slf4j
public class LemmaTypeServiceImpl implements LemmaTypeService {
  private final LemmaTypeRepository lemmaTypeRepository;
  private final TextTokenRepository textTokenRepository;

  public LemmaTypeServiceImpl(LemmaTypeRepository lemmaTypeRepository, TextTokenRepository textTokenRepository) {
    this.lemmaTypeRepository = lemmaTypeRepository;
    this.textTokenRepository = textTokenRepository;
  }

  @Override
  @Transactional
  public List<LemmaType> findByText(String originalText) {
    List<LemmaType> lemmaTypes = lemmaTypeRepository.findAllByTextOrLemmaTokensText(originalText, originalText);
    if (lemmaTypes.isEmpty()) {
      log.error("No LemmaTypes find by {}", originalText);
      return new ArrayList<>();
    }
    log.info("LemmaTypes returned: {}",
            String.join(", ", lemmaTypes.stream()
                    .map(LemmaType::getText)
                    .toList()));
    return lemmaTypes;
  }

  @Override
  public List<ShowMostCommonLemmasDTO> findMostCommonLemmas(short pageNumber, boolean searchKeyword) {
    if (pageNumber < 0) {
      log.error("PageNumber can not be under 0");
      throw new LemmaTypeException("PageNumber can not be under 0");
    }
    if (searchKeyword) {
      List<ShowMostCommonLemmasDTO> showMostCommonLemmasDTOSByKeyWord = lemmaTypeRepository.findMostCommonLemmasInNewsArticles(PageRequest.of(pageNumber, 25, Sort.by("textTokens.size")));
      log.info("Keyword research by most common lemmas. Most common lemmas: {}",
              String.join(", ", showMostCommonLemmasDTOSByKeyWord.stream()
                      .map(ShowMostCommonLemmasDTO::getText)
                      .toList()));
      return showMostCommonLemmasDTOSByKeyWord;
    }
    List<ShowMostCommonLemmasDTO> showMostCommonLemmasDTOS = lemmaTypeRepository.findMostCommonLemmasInNewsArticlesByKeyWords(KeyWordsSingleton.getKeyWords(), PageRequest.of(pageNumber, 25, Sort.by("textTokens.size")));
    log.info("Most common lemmas. Most common lemmas: {}", String.join(", ", showMostCommonLemmasDTOS.stream()
            .map(ShowMostCommonLemmasDTO::getText)
            .toList()));
    return showMostCommonLemmasDTOS;
  }

  @Override
  public List<LemmaOccurenceInSentencesDTO> findLemmasAndOccurencesInSentences(List<Long> sentenceIdsContainingLemma, long lemmaTypeId) {
    List<LemmaOccurenceInSentencesDTO> lemmaOccurenceInSentencesDTOS = lemmaTypeRepository.findLemmaTypeOccurencesInSentences(sentenceIdsContainingLemma, lemmaTypeId);
    if (CollectionUtils.isEmpty(lemmaOccurenceInSentencesDTOS)) {
      log.error("No lemma occurence find in sentences. LemmaType Id: {}", lemmaTypeId);
      throw new LemmaTypeException(String.format("No lemma occurence find in sentences. LemmaType Id: %s", lemmaTypeId));
    }
    log.info("LemmaType: {} Lemma occurences: {}", lemmaTypeId,
            String.join(", ", lemmaOccurenceInSentencesDTOS.stream()
                    .map(LemmaOccurenceInSentencesDTO::getLemmaText)
                    .toList()));
    return lemmaOccurenceInSentencesDTOS;
  }

  @Override
  public LemmaType findById(long lemmaTypeId) {
    Optional<LemmaType> lemmaTypeOptional = lemmaTypeRepository.findById(lemmaTypeId);
    if (lemmaTypeOptional.isEmpty()) {
      log.error("Couldn't find lemmaType with given id: {}", lemmaTypeId);
      throw new LemmaTypeException(String.format("Couldn't find lemmaType with given id: %s", lemmaTypeId));
    }
    log.info("LemmaType find with id: {}", lemmaTypeId);
    return lemmaTypeOptional.get();
  }

  @Override
  public void addTextTokenToLemmaType(long textTokenId, String lemmaTypeIdOrText, String lemmaToken, String phraseType) {
    TextToken textToken = textTokenRepository.findById(textTokenId);
    try {
      textToken.setInvalid(false);
      Long lemmaTypeIdParsed = Long.valueOf(lemmaTypeIdOrText);
      saveTextTokenBelongingToLemmaType(lemmaTypeIdParsed, lemmaToken, textToken, phraseType);
    } catch (NumberFormatException exception) {
      log.info("Provided LemmaType ID or Text was Text");
      addNewTextTokenToLemmaTypeText(lemmaTypeIdOrText, lemmaToken, textToken, phraseType);
    }
  }

  private void addNewTextTokenToLemmaTypeText(String lemmaTypeText, String lemmaToken, TextToken textToken, String phraseType) {
    LemmaType lemmaType = lemmaTypeRepository.findByText(lemmaTypeText);
    if (lemmaType == null) {
      log.info("No Lemma Type found by {} text", lemmaTypeText);
      lemmaType = new LemmaType(textToken.getText());
    }
    if (lemmaToken != null) {
      lemmaType.setLemmaTokens(createLemmaTokens(lemmaToken, lemmaType, phraseType));
    }
      lemmaType.addOneTextToken(textToken);
      textToken.setLemmaType(lemmaType);
      Sentence sentence = textToken.getSentence();
      NewsArticle newsArticle = sentence.getNewsArticle();
      lemmaType.addOneSentence(sentence);
      lemmaType.addOneNewsArticle(newsArticle);
      textToken.setLemmaType(lemmaType);
      if (lemmaToken != null) {
        lemmaType.setLemmaTokens(createLemmaTokens(lemmaToken, lemmaType, phraseType));
      }
      log.info("LemmaToken: {}, PhraseType: {} added to LemmaType Text: {}", lemmaToken, phraseType, lemmaTypeText);
      lemmaTypeRepository.save(lemmaType);
      textTokenRepository.save(textToken);
      checkIfNewLemmaTypeHasMatchingTextTokensSetValidByText(lemmaTypeText, lemmaType.getId());
  }

  private void saveTextTokenBelongingToLemmaType(Long lemmaTypeIdParsed, String lemmaToken, TextToken textToken, String phraseType) {
    LemmaType lemmaType = lemmaTypeRepository.findById(lemmaTypeIdParsed).orElse(null);
    if (lemmaType == null){
      log.info("No Lemma Type found by {} id", lemmaTypeIdParsed);
      lemmaType = new LemmaType(textToken.getText());
    }
    if (lemmaToken != null) {
      lemmaType.setLemmaTokens(createLemmaTokens(lemmaToken, lemmaType, phraseType));
    }
    lemmaType.addOneTextToken(textToken);
    textToken.setPhraseType(phraseType);
    textToken.setLemmaType(lemmaType);
    log.info("LemmaToken: {}, PhraseType: {} added to LemmaType ID: {}", lemmaToken, phraseType, lemmaTypeIdParsed);
    lemmaTypeRepository.save(lemmaType);
    textTokenRepository.save(textToken);
  }

  private void checkIfNewLemmaTypeHasMatchingTextTokensSetValidByText(String lemmaText, long lemmaTypeId) {
    lemmaTypeRepository.updateIfLemmaTypeHasMatchingTextTokens(lemmaText, lemmaTypeId);
  }

  private List<LemmaToken> createLemmaTokens(String lemmaToken, LemmaType lemmaType, String phraseTypeIn) {
    List<LemmaToken> lemmaTokens = new ArrayList<>();
    String[] words = lemmaToken
            .split(";");

    for (String word : words) {
      String wordTrimmed = word.trim();
      CoreLabel tempCoreLabel = CoreLabel.wordFromString(wordTrimmed);
      String phraseType = tempCoreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class);
      if (phraseType == null || phraseType.isEmpty()) {
        phraseType = phraseTypeIn;
      }
      lemmaTokens.add(LemmaToken.builder().lemmaType(lemmaType).text(wordTrimmed).phraseType(phraseType).build());
    }

    return lemmaTokens;
  }

}
