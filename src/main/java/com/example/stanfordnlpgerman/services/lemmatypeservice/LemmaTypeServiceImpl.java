package com.example.stanfordnlpgerman.services.lemmatypeservice;

import com.example.stanfordnlpgerman.exceptions.lemmatypes.LemmaTokenNotFoundByIdException;
import com.example.stanfordnlpgerman.exceptions.validations.NountFoundByIdException;
import com.example.stanfordnlpgerman.models.KeyWordsSingleton;
import com.example.stanfordnlpgerman.models.dao.*;
import com.example.stanfordnlpgerman.models.dtos.lemmatype.ShowMostCommonLemmasDTO;
import com.example.stanfordnlpgerman.models.dtos.sentence.LemmaOccurenceInSentencesDTO;
import com.example.stanfordnlpgerman.repositories.LemmaTypeRepository;
import com.example.stanfordnlpgerman.repositories.TextTokenRepository;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class LemmaTypeServiceImpl implements LemmaTypeService {
  private final LemmaTypeRepository lemmaTypeRepository;
  private final TextTokenRepository textTokenRepository;

  public LemmaTypeServiceImpl(LemmaTypeRepository lemmaTypeRepository, TextTokenRepository textTokenRepository) {
    this.lemmaTypeRepository = lemmaTypeRepository;
    this.textTokenRepository = textTokenRepository;
  }

  @Override
  @Transactional
  public Set<LemmaType> findByText(String originalText) {
    Set<LemmaType> lemmaTypes = lemmaTypeRepository.findAllByTextOrLemmaTokensText(originalText, originalText);
    return lemmaTypes;
  }

  @Override
  public List<ShowMostCommonLemmasDTO> findMostCommonLemmas(short pageNumber, boolean keyword) {
    if (keyword) {
      return lemmaTypeRepository.findMostCommonLemmasInNewsArticles(PageRequest.of(pageNumber, 25, Sort.by("textTokens.size")));
    }
    return lemmaTypeRepository.findMostCommonLemmasInNewsArticlesByKeyWords(KeyWordsSingleton.getKeyWords(), PageRequest.of(pageNumber, 25, Sort.by("textTokens.size")));
  }

  @Override
  public List<LemmaOccurenceInSentencesDTO> findLemmasAndOccurencesInSentences(List<Long> sentenceIdsContainingLemma, long lemmaTypeId) {
    return lemmaTypeRepository.findLemmaTypeOccurencesInSentences(sentenceIdsContainingLemma, lemmaTypeId);
  }

  @Override
  public void addTextTokenToLemmaType(long textTokenId, String lemmaTypeIdOrText, String lemmaToken, String phraseType) throws NountFoundByIdException {
    TextToken textToken = textTokenRepository.findById(textTokenId);
    try {
      textToken.setInvalid(false);
      Long lemmaTypeIdParsed = Long.parseLong(lemmaTypeIdOrText);
      LemmaType lemmaType = lemmaTypeRepository.findById(lemmaTypeIdParsed).orElse(null);
      lemmaType.addOneTextToken(textToken);
      textToken.setLemmaType(lemmaType);
      lemmaTypeRepository.save(lemmaType);

    } catch (NullPointerException e) {
      throw new NountFoundByIdException("Could find given object by id, cause: " + e.getCause());
    } catch (NumberFormatException e) {
      addNewTextTokenToLemmaType(lemmaTypeIdOrText, lemmaToken, textToken, phraseType);
    }
  }

  private void addNewTextTokenToLemmaType(String lemmaTypeIdOrText, String lemmaToken, TextToken textToken, String phraseType) {
    if (!lemmaTypeRepository.existsLemmaTypeByText(lemmaTypeIdOrText)) {
      LemmaType lemmaType = new LemmaType(textToken.getText());
      lemmaType.addOneTextToken(textToken);
      textToken.setLemmaType(lemmaType);
      Sentence sentence = textToken.getSentence();
      NewsArticle newsArticle = sentence.getNewsArticle();
      lemmaType.addOneSentence(sentence);
      lemmaType.addOneNewsArticle(newsArticle);
      textToken.setLemmaType(lemmaType);
      if (lemmaToken != null || !lemmaToken.isEmpty()) {
        lemmaType.setLemmaTokens(createLemmaTokens(lemmaToken, lemmaType, phraseType));
      }
      lemmaTypeRepository.save(lemmaType);
      textTokenRepository.save(textToken);
      checkIfNewLemmaTypeHasMatchingTextTokensSetValidByText(lemmaTypeIdOrText, lemmaType.getId());
    }
  }

  @Override
  public LemmaType findById(long lemmaTypeId) throws LemmaTokenNotFoundByIdException {
    Optional<LemmaType> lemmaTypeOptional = lemmaTypeRepository.findById(lemmaTypeId);
    if (lemmaTypeOptional.get() == null) {
      throw new LemmaTokenNotFoundByIdException("Couldn't find lemmaType with given id");
    }
    return lemmaTypeOptional.get();
  }

  private void checkIfNewLemmaTypeHasMatchingTextTokensSetValidByText(String lemmaText, long lemmaTypeId) {
    lemmaTypeRepository.updateIfLemmaTypeHasMatchingTextTokens(lemmaText, lemmaTypeId);
  }

  private Set<LemmaToken> createLemmaTokens(String lemmaToken, LemmaType lemmaType, String phraseTypeIn) {
    Set<LemmaToken> lemmaTokens = new HashSet<>();
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
