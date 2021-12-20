package com.example.stanfordnlpgerman.services.lemmatypeservice;

import com.example.stanfordnlpgerman.exceptions.lemmatypes.LemmaTokenNotFoundByIdException;
import com.example.stanfordnlpgerman.exceptions.validations.NountFoundByIdException;
import com.example.stanfordnlpgerman.models.dao.LemmaType;
import com.example.stanfordnlpgerman.models.dao.NewsArticle;
import com.example.stanfordnlpgerman.models.dao.Sentence;
import com.example.stanfordnlpgerman.models.dao.TextToken;
import com.example.stanfordnlpgerman.models.dtos.lemmatype.ShowMostCommonLemmasDTO;
import com.example.stanfordnlpgerman.models.dtos.sentence.LemmaOccurenceInSentencesDTO;
import com.example.stanfordnlpgerman.repositories.LemmaTypeRepository;
import com.example.stanfordnlpgerman.repositories.TextTokenRepository;
import com.example.stanfordnlpgerman.services.lemmatokenservice.LemmaTokenService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class LemmaTypeServiceImpl implements LemmaTypeService {
  private final LemmaTypeRepository lemmaTypeRepository;
  private final LemmaTokenService lemmaTokenService;
  private final TextTokenRepository textTokenRepository;

  public LemmaTypeServiceImpl(LemmaTypeRepository lemmaTypeRepository, LemmaTokenService lemmaTokenService, TextTokenRepository textTokenRepository) {
    this.lemmaTypeRepository = lemmaTypeRepository;
    this.lemmaTokenService = lemmaTokenService;
    this.textTokenRepository = textTokenRepository;
  }

  @Override
  @Transactional
  public Set<LemmaType> findByText(String originalText) {
    return lemmaTypeRepository.findAllByText(originalText);
  }

  @Override
  public List<ShowMostCommonLemmasDTO> findMostCommonLemmas(short pageNumber) {
    return lemmaTypeRepository.findMostCommonLemmasInNewsArticles(PageRequest.of(pageNumber, 25, Sort.by("textTokens.size")));
  }

  @Override
  public List<LemmaOccurenceInSentencesDTO> findLemmasAndOccurencesInSentences(List<Long> sentenceIdsContainingLemma, long lemmaTypeId) {
    return lemmaTypeRepository.findLemmaTypeOccurencesInSentences(sentenceIdsContainingLemma, lemmaTypeId);
  }

  @Override
  public void addTextTokenToLemmaType(long textTokenId, String lemmaTypeIdOrText) throws NountFoundByIdException {
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
      if (!lemmaTypeRepository.existsLemmaTypeByText(lemmaTypeIdOrText)) {
        LemmaType lemmaType = new LemmaType(textToken.getText());
        lemmaType.addOneTextToken(textToken);
        textToken.setLemmaType(lemmaType);
        Sentence sentence = textToken.getSentence();
        NewsArticle newsArticle = sentence.getNewsArticle();
        lemmaType.addOneSentence(sentence);
        lemmaType.addOneNewsArticle(newsArticle);
        lemmaTypeRepository.save(lemmaType);
        checkIfNewLemmaTypeHasMatchingTextTokensSetValid(lemmaTypeIdOrText, lemmaType.getId());
      }
    }
  }

  @Override
  public LemmaType findById(long lemmaTypeId) throws LemmaTokenNotFoundByIdException {
    Optional<LemmaType> lemmaTypeOptional =  lemmaTypeRepository.findById(lemmaTypeId);
    if (lemmaTypeOptional.get() == null){
      throw new LemmaTokenNotFoundByIdException("Couldn't find lemmaType with given id");
    }
    return lemmaTypeOptional.get();
  }

  private void checkIfNewLemmaTypeHasMatchingTextTokensSetValid(String lemmaText, long lemmaTypeId) {
    lemmaTypeRepository.updateIfLemmaTypeHasMatchingTextTokens(lemmaText, lemmaTypeId);
  }
}
