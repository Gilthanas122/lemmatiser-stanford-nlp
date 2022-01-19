package com.example.stanfordnlpgerman.services.sentenceservice;

import com.example.stanfordnlpgerman.exceptions.lemmatypes.LemmaTokenNotFoundByIdException;
import com.example.stanfordnlpgerman.models.dao.LemmaType;
import com.example.stanfordnlpgerman.models.dao.NewsArticle;
import com.example.stanfordnlpgerman.models.dao.Sentence;
import com.example.stanfordnlpgerman.models.dao.TextToken;
import com.example.stanfordnlpgerman.models.dtos.sentence.AdjacentSentencesToInvalidDTO;
import com.example.stanfordnlpgerman.models.dtos.sentence.InvalidSentencesDTO;
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

  @Override
  public List<InvalidSentencesDTO> getInvalidSentences() {
    return sentenceRepository.getInvalidSentences();
  }

  @Override
  public AdjacentSentencesToInvalidDTO getAdjacentSentences(long id, String text) {
    NewsArticle newsArticle = sentenceRepository.findNewsArticleBySentenceId(id);

    AdjacentSentencesToInvalidDTO adjacentSentencesToInvalidDTO = new AdjacentSentencesToInvalidDTO();
    StringBuilder sentenceText = new StringBuilder();
    Collections.sort(newsArticle.getSentences());
    newsArticle.getSentences().stream().sorted().map(Sentence::getText).forEach(sentenceText::append);

    adjacentSentencesToInvalidDTO.setSentencesText(sentenceText.toString());
    adjacentSentencesToInvalidDTO.setOriginalSentenceId(id);
    adjacentSentencesToInvalidDTO.setOriginalSentenceText(text);
    return adjacentSentencesToInvalidDTO;
  }

  @Override
  public void fixInvalidSentences(int operation, long id) throws Exception {
    switch (operation){
      case 1:
        makeSentenceValidWithoutMerging(id);
        break;
      case 2:
          mergeWithOneSentence(id, true);
          break;
      case 3:
        mergeWithOneSentence(id, false);
        break;
      case 4:
        mergeWithAllAdjacentSentence(id);
        break;
      default:
        throw new Exception("Invalid operation");
    }
  }

  private void mergeWithAllAdjacentSentence(long id) {
  }

  private void mergeWithLatterSentence(long id) {
  }

  private void mergeWithOneSentence(long id, boolean isItPreviousToMergeWith) {
    NewsArticle newsArticle = sentenceRepository.findNewsArticleBySentenceId(id);
    Collections.sort(newsArticle.getSentences());
    short actualPosition = 0;
    boolean changeAfterThis = false;

    for (int i = 0; i <newsArticle.getSentences().size(); i++) {
      Sentence sentence = newsArticle.getSentences().get(i);
      if (sentence.getId() == id){
        Sentence sentenceToDelete = new Sentence();
        if(isItPreviousToMergeWith){
          actualPosition = (short) (sentence.getTextPosition() -1);
          sentenceToDelete = newsArticle.getSentences().get(i -1);
          sentenceToDelete.setDeleted(true);
        }else{
          sentenceToDelete = newsArticle.getSentences().get(i + 1);
          sentenceToDelete.setDeleted(true);
          actualPosition = (short) (sentence.getTextPosition());
          i++;
        }
        sentence = inheritDataFromDeletedSentence(sentence, sentenceToDelete, isItPreviousToMergeWith);
        sentence.setInvalid(false);
        changeAfterThis = true;
      }
      if (changeAfterThis){
        sentence.setTextPosition(actualPosition);
        actualPosition++;
        sentenceRepository.save(sentence);
      }
    }
  }

  private Sentence inheritDataFromDeletedSentence(Sentence sentence, Sentence sentenceToDelete, boolean isItFirst) {
    List<LemmaType> lemmaTypesFromToDelete = updateLemmaTypes(sentence, sentenceToDelete.getLemmaTypes(), sentenceToDelete);
    List<TextToken> textTokensFromToDelete = updateTextTokens(sentence, sentenceToDelete.getTextTokens());
    String sentenceText = sentenceToDelete.getText();
    sentenceToDelete.setLemmaTypes(new ArrayList<>());

    List<LemmaType> sentenceLemmaTypes = sentence.getLemmaTypes();
    List<TextToken> sentenceTextTokens = sentence.getTextTokens();
    String concatString = "";
    if (isItFirst){
      concatString = sentenceText.concat(" " + sentence.getText());
    }else{
      concatString = sentence.getText().concat(" " + sentenceText);
    }
    sentenceLemmaTypes.addAll(lemmaTypesFromToDelete);
    sentenceTextTokens.addAll(textTokensFromToDelete);

    sentence.setLemmaTypes(sentenceLemmaTypes);
    sentence.setTextTokens(sentenceTextTokens);
    sentence.setText(concatString);
    return sentence;
  }

  private List<TextToken> updateTextTokens(Sentence sentence, List<TextToken> textTokens) {
    return textTokens
            .stream()
            .map(s-> {
              s.setSentence(sentence);
              return s;
            })
            .collect(Collectors.toList());
  }

  private List<LemmaType> updateLemmaTypes(Sentence sentence, List<LemmaType> lemmaTypesFromToDelete, Sentence sentenceToDelete) {
    return lemmaTypesFromToDelete
            .stream()
            .map(s-> {
              s.addOneSentence(sentence);
              s.removeOneSentence(sentenceToDelete);
              return s;
            })
            .collect(Collectors.toList());
  }

  private void makeSentenceValidWithoutMerging(long id) {
    sentenceRepository.makeSentenceValidById(id);
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
