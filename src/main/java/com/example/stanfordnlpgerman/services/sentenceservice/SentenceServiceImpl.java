package com.example.stanfordnlpgerman.services.sentenceservice;

import com.example.stanfordnlpgerman.exceptions.lemmatypes.LemmaTokenNotFoundByIdException;
import com.example.stanfordnlpgerman.models.dao.LemmaType;
import com.example.stanfordnlpgerman.models.dao.TextToken;
import com.example.stanfordnlpgerman.models.dtos.sentence.LemmaOccurenceInSentencesDTO;
import com.example.stanfordnlpgerman.models.dtos.sentence.SentenceTextAndNewsPaperIdDTO;
import com.example.stanfordnlpgerman.repositories.SentenceRepository;
import com.example.stanfordnlpgerman.services.lemmatypeservice.LemmaTypeService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SentenceServiceImpl implements SentenceService {
  private final SentenceRepository sentenceRepository;
  private final LemmaTypeService lemmaTypeService;

  public SentenceServiceImpl(SentenceRepository sentenceRepository, LemmaTypeService lemmaTypeService) {
    this.sentenceRepository = sentenceRepository;
    this.lemmaTypeService = lemmaTypeService;
  }

  @Override
  public List<SentenceTextAndNewsPaperIdDTO> getAllSentencesBelongingToLemmaType(long lemmaTypId) {
    return sentenceRepository.findByLemmaTypeId(lemmaTypId);
  }

  @Override
  public List<LemmaOccurenceInSentencesDTO> showWordsInContext(long lemmaTypeId, int distance) throws LemmaTokenNotFoundByIdException {
    if (distance < 1){
      return getContextFromFullSentence(lemmaTypeId);
    }

    LemmaType researchLemmaType = lemmaTypeService.findById(lemmaTypeId);

    List<TextToken> textTokens = sentenceRepository.sentencesContainingLemma(lemmaTypeId);

    List<Long> textTokenIds = getTextTokenIdsFromSentences(textTokens, lemmaTypeId);

    Map<String, Long> textTokensAndOccurences = filterTextTokensBasedOnDistance(textTokens, textTokenIds, distance, researchLemmaType.getText());

    return createFromMapListLemmaOccurenceInSentencesDTO(textTokensAndOccurences, researchLemmaType.getText());

  }

  private List<LemmaOccurenceInSentencesDTO> createFromMapListLemmaOccurenceInSentencesDTO(Map<String, Long> textTokensAndOccurences, String lemmaTypeText) {
    List<LemmaOccurenceInSentencesDTO> lemmaOccurenceInSentencesDTOS = new LinkedList<>();
    for (Map.Entry<String, Long> entry : textTokensAndOccurences.entrySet()) {
      lemmaOccurenceInSentencesDTOS.add(createLemmaOccurenceInSentenceDTO(entry.getKey(), entry.getValue()));
    }

    lemmaOccurenceInSentencesDTOS.add(0, createLemmaOccurenceInSentenceDTO(lemmaTypeText, 0L));
    return lemmaOccurenceInSentencesDTOS;
  }

  private LemmaOccurenceInSentencesDTO createLemmaOccurenceInSentenceDTO(String key, Long value) {
    return new LemmaOccurenceInSentencesDTO() {
      @Override
      public String getOriginalLemmaText() {
        return key;
      }

      @Override
      public String getLemmaText() {
        return key;
      }

      @Override
      public Long getLemmaOccurence() {
        return value;
      }
    };
  }

  private Map<String, Long> filterTextTokensBasedOnDistance(List<TextToken> textTokens, List<Long> textTokenIds, int distance, String lemmaTypeText) {
   List<String> ll = new ArrayList<>();
    int textTokenCounter = 0;
    Map<String, Long> textTokensAndOccurences = new HashMap<>();
    for (int i = 0; i < textTokens.size(); i++) {
      if (textTokenCounter >= textTokenIds.size()) {
        break;
      } else if (textTokens.get(i).getId() == textTokenIds.get(textTokenCounter)) {
        for (int j = 0; j <= distance * 2; j++) {
          if (i - distance + j < 0) {
            continue;
          } else if (textTokens.get(i).getSentence().getId() != textTokens.get(i - distance + j).getSentence().getId() || i - distance + j >= textTokens.size()) {
            if (j > distance) {
              break;
            }
            continue;
          } else {
            TextToken currentTextToken = textTokens.get(i - distance + j);
            if (currentTextToken.getLemmaType() == null || currentTextToken.getLemmaType().getText().equals(lemmaTypeText)){
              continue;
            }else if (textTokensAndOccurences.containsKey(currentTextToken.getLemmaType().getText())) {
              textTokensAndOccurences.put(currentTextToken.getLemmaType().getText(), textTokensAndOccurences.get(currentTextToken.getLemmaType().getText()) + 1);
            } else {
              textTokensAndOccurences.put(currentTextToken.getLemmaType().getText(), 1l);
            }
          }
        }
        textTokenCounter++;
      }
    }
    return textTokensAndOccurences;
  }

  private List<Long> getTextTokenIdsFromSentences(List<TextToken> textTokens, long lemmaTypeId) {
   return textTokens
            .stream()
            .filter(tt -> tt.getLemmaType() != null)
            .filter(tt -> tt.getLemmaType().getId() == lemmaTypeId)
            .map(TextToken::getId)
            .collect(Collectors.toList());
  }

  private List<LemmaOccurenceInSentencesDTO> getContextFromFullSentence(long lemmaTypeId) {
    List<Long> sentenceIdsContainingLemma = sentenceRepository.sentenceIdsContainingLemma(lemmaTypeId);
    List<LemmaOccurenceInSentencesDTO> lemmasAndOccurences = lemmaTypeService.findLemmasAndOccurencesInSentences(sentenceIdsContainingLemma, lemmaTypeId);
    return lemmasAndOccurences;
  }
}
