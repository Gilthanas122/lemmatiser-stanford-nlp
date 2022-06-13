package com.example.stanfordnlpgerman.lemmatypeservice;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.example.stanfordnlpgerman.models.KeyWordsSingleton;
import com.example.stanfordnlpgerman.models.dao.LemmaType;
import com.example.stanfordnlpgerman.models.dao.TextToken;
import com.example.stanfordnlpgerman.models.dtos.lemmatype.ShowMostCommonLemmasDTO;
import com.example.stanfordnlpgerman.models.dtos.lemmatype.UpdateLemmaTypeRequest;
import com.example.stanfordnlpgerman.models.dtos.sentence.LemmaOccurenceInSentencesDTO;
import com.example.stanfordnlpgerman.repositories.LemmaTypeRepository;
import com.example.stanfordnlpgerman.repositories.TextTokenRepository;
import com.example.stanfordnlpgerman.services.lemmatypeservice.LemmaTypeException;
import com.example.stanfordnlpgerman.services.lemmatypeservice.LemmaTypeServiceImpl;
import com.example.stanfordnlpgerman.testobjectcreation.LemmaTypeCreator;
import com.example.stanfordnlpgerman.testobjectcreation.TextTokenCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LemmaTypeServiceTest {
  private final Logger logger = (Logger) LoggerFactory.getLogger(LemmaTypeServiceImpl.class);
  private final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
  @Mock
  private LemmaTypeRepository lemmaTypeRepository;
  @Mock
  private TextTokenRepository textTokenRepository;
  @InjectMocks
  private LemmaTypeServiceImpl lemmaTypeService;

  @BeforeEach
  void setup(){
    listAppender.start();
    logger.addAppender(listAppender);
  }

  @AfterEach
  void tearDown(){
    logger.detachAppender(listAppender);
  }

  @Test
  void findByText_success_shouldReturnListLemmaType(){
    String lemmaText = "lemmaText";
    Set<LemmaType> expectedLemmaTypes = LemmaTypeCreator.createLemmaTypes(2);

    when(lemmaTypeRepository.findAllByLemmaText(lemmaText)).thenReturn(expectedLemmaTypes);

    List<ILoggingEvent> logsList = listAppender.list;

    Set<LemmaType> actualLemmaTypes = lemmaTypeService.findAllByText(lemmaText);

    assertEquals(1, logsList.size());
    assertTrue(logsList.get(0).getFormattedMessage().contains("LemmaTypes returned"));
    assertTrue(logsList.get(0).getFormattedMessage().contains("Lemma type text2"));
    assertTrue(logsList.get(0).getFormattedMessage().contains("Lemma type text1"));
    assertEquals(expectedLemmaTypes.size(), actualLemmaTypes.size());
    assertEquals(expectedLemmaTypes, actualLemmaTypes);
  }

  @Test
  void findByText_NoLemmaTypesFound_shouldLogNoLemmaTypesFound(){
    String lemmaText = "lemmaText";
    Set<LemmaType> expectedLemmaTypes = new HashSet<>();

    when(lemmaTypeRepository.findAllByLemmaText(lemmaText)).thenReturn(expectedLemmaTypes);

    List<ILoggingEvent> logsList = listAppender.list;

    Set<LemmaType> actualLemmaTypes = lemmaTypeService.findAllByText(lemmaText);

    assertEquals(1, logsList.size());
    assertEquals("No LemmaTypes find by " + lemmaText, logsList.get(0).getFormattedMessage());
    assertEquals(expectedLemmaTypes.size(), actualLemmaTypes.size());
    assertEquals(expectedLemmaTypes, actualLemmaTypes);
  }

  @Test
  void findMostCommonLemma_successWithKeyWordTrue_shouldReturnListShowMostCommonLemmasDTO(){
    short pageNumber = 1;
    List<ShowMostCommonLemmasDTO> expectedShowMostCommonLemmasDTOS = createShowMostCommonLemmas();

    when(lemmaTypeRepository.findMostCommonLemmasInNewsArticlesByKeyWords(KeyWordsSingleton.getKeyWords(), PageRequest.of(pageNumber, 25, Sort.by("textTokens.size"))))
            .thenReturn(expectedShowMostCommonLemmasDTOS);

    List<ShowMostCommonLemmasDTO> actualShowMostCommonLemmasDTOS = lemmaTypeService.findMostCommonLemmas(pageNumber, true);

    List<ILoggingEvent> logsList = listAppender.list;

    assertEquals(1, logsList.size());
    assertEquals("Most common lemmas: show most common lemma text 1, show most common lemma text 2", logsList.get(0).getFormattedMessage());
    assertEquals(expectedShowMostCommonLemmasDTOS.size(), actualShowMostCommonLemmasDTOS.size());
    assertShowMostCommonLemmas(expectedShowMostCommonLemmasDTOS.get(0), actualShowMostCommonLemmasDTOS.get(0));
    assertShowMostCommonLemmas(expectedShowMostCommonLemmasDTOS.get(1), actualShowMostCommonLemmasDTOS.get(1));
  }

  @Test
  void findMostCommonLemma_successWithKeyWordFalse_shouldReturnListShowMostCommonLemmasDTO(){
    short pageNumber = 1;
    List<ShowMostCommonLemmasDTO> expectedShowMostCommonLemmasDTOS = createShowMostCommonLemmas();

    when(lemmaTypeRepository.findMostCommonLemmasInNewsArticles(PageRequest.of(pageNumber, 25, Sort.by("textTokens.size"))))
            .thenReturn(expectedShowMostCommonLemmasDTOS);

    List<ShowMostCommonLemmasDTO> actualShowMostCommonLemmasDTOS = lemmaTypeService.findMostCommonLemmas(pageNumber, false);

    List<ILoggingEvent> logsList = listAppender.list;

    assertEquals(1, logsList.size());
    assertEquals("Most common lemmas: show most common lemma text 1, show most common lemma text 2", logsList.get(0).getFormattedMessage());
    assertEquals(expectedShowMostCommonLemmasDTOS.size(), actualShowMostCommonLemmasDTOS.size());
    assertShowMostCommonLemmas(expectedShowMostCommonLemmasDTOS.get(0), actualShowMostCommonLemmasDTOS.get(0));
    assertShowMostCommonLemmas(expectedShowMostCommonLemmasDTOS.get(1), actualShowMostCommonLemmasDTOS.get(1));
  }

  @Test
  void findMostCommonLemma_withPageNumberBelow0_shouldThrowLemmaTypeException(){
    short pageNumber = -1;

    List<ILoggingEvent> logsList = listAppender.list;
    LemmaTypeException exception = assertThrows(LemmaTypeException.class, () -> lemmaTypeService.findMostCommonLemmas(pageNumber, true));

    assertEquals(1, logsList.size());
    assertEquals("PageNumber can not be under 0", logsList.get(0).getFormattedMessage());
    assertEquals("PageNumber can not be under 0", exception.getMessage());
  }

  @Test
  void findLemmasAndOccurencesInSentences_success_shouldReturnLemmaOccurenceInSentencesDTOList(){
    List<LemmaOccurenceInSentencesDTO> expectedLemmaOccurenceInSentencesDTOS = createLemmaOccurenceInSentencesDTOS();
    List<Long> sentenceIdsContainingLemma = List.of(1L, 2L);
    long lemmaTypeId = 1L;

    when(lemmaTypeRepository.findLemmaTypeOccurencesInSentences(sentenceIdsContainingLemma, lemmaTypeId)).thenReturn(expectedLemmaOccurenceInSentencesDTOS);

    List<ILoggingEvent> logsList = listAppender.list;

    List<LemmaOccurenceInSentencesDTO> actualLemmaOccurenceInSentencesDTOS = lemmaTypeService.findLemmasAndOccurencesInSentences(sentenceIdsContainingLemma, lemmaTypeId);

    assertEquals(1, logsList.size());
    assertEquals("LemmaType: 1 Lemma occurences: lemma text 1, lemma text 2", logsList.get(0).getFormattedMessage());
    assertEquals(expectedLemmaOccurenceInSentencesDTOS.size(), actualLemmaOccurenceInSentencesDTOS.size());
    assertLemmaOccurenceInSentencesDTO(expectedLemmaOccurenceInSentencesDTOS.get(0), actualLemmaOccurenceInSentencesDTOS.get(0));
    assertLemmaOccurenceInSentencesDTO(expectedLemmaOccurenceInSentencesDTOS.get(1), actualLemmaOccurenceInSentencesDTOS.get(1));
  }

  @Test
  void findLemmasAndOccurencesInSentences_lemmaTypeRepositoryReturnsEmptySentence_shouldThrowLemmaTypeException(){
    List<Long> sentenceIdsContainingLemma = List.of(1L, 2L);
    long lemmaTypeId = 1L;

    when(lemmaTypeRepository.findLemmaTypeOccurencesInSentences(sentenceIdsContainingLemma, lemmaTypeId)).thenReturn(new ArrayList<>());

    List<ILoggingEvent> logsList = listAppender.list;

    LemmaTypeException exception = assertThrows(LemmaTypeException.class, () -> lemmaTypeService.findLemmasAndOccurencesInSentences(sentenceIdsContainingLemma, lemmaTypeId));

    assertEquals(1, logsList.size());
    assertEquals("No lemma occurence find in sentences. LemmaType Id: 1", logsList.get(0).getFormattedMessage());
    assertEquals("No lemma occurence find in sentences. LemmaType Id: 1", exception.getMessage());
  }

  @Test
  void addTextTokenToLemmaType_successWithLemmaTypeIdOrTextAsId_shouldLogAddedToLemmaType(){
    long textTokenId = 1L;
    UpdateLemmaTypeRequest updateLemmaTypeRequest =
        UpdateLemmaTypeRequest.builder()
            .lemmaTypeId("2")
            .phraseType("VERB")
            .lemmaToken("lemmaToken")
            .build();
    LemmaType expectedLemmaType = LemmaTypeCreator.createLemmaType(1);
    TextToken textToken = TextTokenCreator.createTextToken(1);

    when(textTokenRepository.findById(textTokenId)).thenReturn(textToken);
    when(lemmaTypeRepository.findById(Long.parseLong(updateLemmaTypeRequest.getLemmaTypeId()))).thenReturn(Optional.ofNullable(expectedLemmaType));

    List<ILoggingEvent> logsList = listAppender.list;

    lemmaTypeService.addTextTokenToLemmaType(textTokenId, updateLemmaTypeRequest);

    assertEquals(1, logsList.size());
    assertEquals("LemmaToken: lemmaToken, PhraseType: VERB added to LemmaType ID: 2", logsList.get(0).getFormattedMessage());
    verify(lemmaTypeRepository, times(1)).save(expectedLemmaType);
  }

  @Test
  void addTextTokenToLemmaType_successWithLemmaTypeIdOrTextAsTextAndLemmaTypeNotFoundInRepository_shouldLogAddedToLemmaType(){
    long textTokenId = 1L;
    UpdateLemmaTypeRequest updateLemmaTypeRequest = UpdateLemmaTypeRequest.builder()
        .lemmaTypeId("text")
        .lemmaToken("lemmaToken")
        .phraseType("VERB")
        .build();

    TextToken textToken = TextTokenCreator.createTextToken(1);

    when(textTokenRepository.findById(textTokenId)).thenReturn(textToken);

    List<ILoggingEvent> logsList = listAppender.list;

    lemmaTypeService.addTextTokenToLemmaType(textTokenId, updateLemmaTypeRequest);

    assertEquals(3, logsList.size());
    assertEquals("Provided LemmaType ID or Text was Text", logsList.get(0).getFormattedMessage());
    assertEquals(String.format("No Lemma Type found by %s text", updateLemmaTypeRequest.getLemmaTypeId()), logsList.get(1).getFormattedMessage());
    assertEquals("LemmaToken: lemmaToken, PhraseType: VERB added to LemmaType Text: " + updateLemmaTypeRequest.getLemmaTypeId(), logsList.get(2).getFormattedMessage());
    verify(lemmaTypeRepository, times(1)).save(any());
  }

  @Test
  void addTextTokenToLemmaType_successWithLemmaTypeIdOrTextAsTextAndLemmaTypeExistByText_shouldLogAddedToLemmaType(){
    long textTokenId = 1L;
    UpdateLemmaTypeRequest updateLemmaTypeRequest = UpdateLemmaTypeRequest.builder()
        .lemmaTypeId("text")
        .lemmaToken("lemmaToken")
        .phraseType("VERB")
        .build();

    LemmaType expectedLemmaType = LemmaTypeCreator.createLemmaType(1);
    TextToken textToken = TextTokenCreator.createTextToken(1);

    when(textTokenRepository.findById(textTokenId)).thenReturn(textToken);
    when(lemmaTypeRepository.findByText(updateLemmaTypeRequest.getLemmaTypeId())).thenReturn(expectedLemmaType);

    List<ILoggingEvent> logsList = listAppender.list;

    lemmaTypeService.addTextTokenToLemmaType(textTokenId, updateLemmaTypeRequest);

    assertEquals(2, logsList.size());
    assertEquals("Provided LemmaType ID or Text was Text", logsList.get(0).getFormattedMessage());
    assertEquals("LemmaToken: lemmaToken, PhraseType: VERB added to LemmaType Text: text", logsList.get(1).getFormattedMessage());
    verify(lemmaTypeRepository, times(1)).save(expectedLemmaType);
  }

  @Test
  void addTextTokenToLemmaType_successWithLemmaTypeIdOrTextAsTextAndLemmaTypeDoesNotExistByText_shouldLogAddedToLemmaType(){
    long textTokenId = 1L;
    UpdateLemmaTypeRequest updateLemmaTypeRequest = UpdateLemmaTypeRequest.builder()
        .lemmaTypeId("text")
        .lemmaToken("lemmaToken")
        .phraseType("VERB")
        .build();

    TextToken textToken = TextTokenCreator.createTextToken(1);

    when(textTokenRepository.findById(textTokenId)).thenReturn(textToken);
    when(lemmaTypeRepository.findByText(updateLemmaTypeRequest.getLemmaTypeId())).thenReturn(null);

    List<ILoggingEvent> logsList = listAppender.list;

    lemmaTypeService.addTextTokenToLemmaType(textTokenId, updateLemmaTypeRequest);

    assertEquals(3, logsList.size());
    assertEquals("Provided LemmaType ID or Text was Text", logsList.get(0).getFormattedMessage());
    assertEquals("No Lemma Type found by " + updateLemmaTypeRequest.getLemmaTypeId() + " text", logsList.get(1).getFormattedMessage());
    assertEquals("LemmaToken: lemmaToken, PhraseType: VERB added to LemmaType Text: " + updateLemmaTypeRequest.getLemmaTypeId(), logsList.get(2).getFormattedMessage());
    verify(lemmaTypeRepository, times(1)).save(any());
  }

  @Test
  void addTextTokenToLemmaType_successWithLemmaTypeIdOrTextAsIdAndLemmaTypeDoesNotExistByText_shouldLogAddedToLemmaType(){
    long textTokenId = 1L;
    UpdateLemmaTypeRequest updateLemmaTypeRequest = UpdateLemmaTypeRequest.builder()
        .lemmaTypeId("1")
        .lemmaToken("lemmaToken")
        .phraseType("VERB")
        .build();

    TextToken textToken = TextTokenCreator.createTextToken(1);

    when(textTokenRepository.findById(textTokenId)).thenReturn(textToken);

    List<ILoggingEvent> logsList = listAppender.list;

    lemmaTypeService.addTextTokenToLemmaType(textTokenId, updateLemmaTypeRequest);

    assertEquals(2, logsList.size());
    assertEquals("No Lemma Type found by " + updateLemmaTypeRequest.getLemmaTypeId() + " id", logsList.get(0).getFormattedMessage());
    assertEquals("LemmaToken: lemmaToken, PhraseType: VERB added to LemmaType ID: " + updateLemmaTypeRequest.getLemmaTypeId(), logsList.get(1).getFormattedMessage());
    verify(lemmaTypeRepository, times(1)).save(any());
    verify(textTokenRepository, times(1)).save(textToken);
  }

  @Test
  void findById_success_shouldReturnLemmaType(){
    LemmaType expectedLemmaType = LemmaTypeCreator.createLemmaType(1);
    long lemmaTypeId = 1L;

    List<ILoggingEvent> logsList = listAppender.list;

    when(lemmaTypeRepository.findById(lemmaTypeId)).thenReturn(Optional.ofNullable(expectedLemmaType));

    LemmaType actualLemmaType = lemmaTypeService.findById(lemmaTypeId);

    assertEquals(1, logsList.size());
    assertEquals("LemmaType find with id: " + lemmaTypeId, logsList.get(0).getFormattedMessage());
    assertLemmaType(expectedLemmaType, actualLemmaType);
  }

  @Test
  void findById_lemmaTypeRepositoryReturnNull_shouldReturnLemmaType(){
    long lemmaTypeId = 1L;

    List<ILoggingEvent> logsList = listAppender.list;

    when(lemmaTypeRepository.findById(lemmaTypeId)).thenReturn(Optional.empty());

    LemmaTypeException exception = assertThrows(LemmaTypeException.class, () -> lemmaTypeService.findById(lemmaTypeId));

    assertEquals(1, logsList.size());
    assertEquals("Couldn't find lemmaType with given id: " + lemmaTypeId, logsList.get(0).getFormattedMessage());
    assertEquals("Couldn't find lemmaType with given id: " + lemmaTypeId, exception.getMessage());
  }

  private void assertLemmaOccurenceInSentencesDTO(LemmaOccurenceInSentencesDTO expected, LemmaOccurenceInSentencesDTO actual) {
    assertEquals(expected.getLemmaOccurence(), actual.getLemmaOccurence());
    assertEquals(expected.getLemmaText(), actual.getLemmaText());
    assertEquals(expected.getOriginalLemmaText(), actual.getOriginalLemmaText());
  }

  private List<LemmaOccurenceInSentencesDTO> createLemmaOccurenceInSentencesDTOS() {
    List<LemmaOccurenceInSentencesDTO> lemmaOccurenceInSentencesDTOS = new ArrayList<>();
    for (int index = 1; index <= 2; index++) {
      lemmaOccurenceInSentencesDTOS.add(createLemmaOccurenceInSentencesDTO(index));
    }
    return lemmaOccurenceInSentencesDTOS;
  }

  private LemmaOccurenceInSentencesDTO createLemmaOccurenceInSentencesDTO(int index) {
    return LemmaOccurenceInSentencesDTO.builder()
            .originalLemmaText("original lemma text " + index)
            .lemmaText("lemma text " + index)
            .lemmaOccurence(index)
            .build();
  }

  private void assertShowMostCommonLemmas(ShowMostCommonLemmasDTO expected, ShowMostCommonLemmasDTO actual) {
    assertEquals(expected.getLemmaTypeId(), actual.getLemmaTypeId());
    assertEquals(expected.getCount(), actual.getCount());
    assertEquals(expected.getNewsArticleId(), actual.getNewsArticleId());
    assertEquals(expected.getText(), actual.getText());
  }

  private List<ShowMostCommonLemmasDTO> createShowMostCommonLemmas() {
    List<ShowMostCommonLemmasDTO> showMostCommonLemmasDTOS = new ArrayList<>();
    for (int index = 1; index <= 2; index++) {
      showMostCommonLemmasDTOS.add(createShowMostCommonLemma(index));
    }
    return showMostCommonLemmasDTOS;
  }

  private ShowMostCommonLemmasDTO createShowMostCommonLemma(int index) {
     return new ShowMostCommonLemmasDTO() {
       @Override
       public long getLemmaTypeId() {
         return index;
       }

       @Override
       public String getText() {
         return "show most common lemma text "+ index;
       }

       @Override
       public int getCount() {
         return index;
       }

       @Override
       public long getNewsArticleId() {
         return 0;
       }
     };
  }

  private void assertLemmaType(LemmaType expected, LemmaType actual) {
    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getText(), actual.getText());
    assertEquals(expected.isDeleted(), actual.isDeleted());
    assertEquals(expected.getSentences(), actual.getSentences());
    assertEquals(expected.getNewsArticles(), actual.getNewsArticles());
    assertEquals(expected.getLemmaTokens(), actual.getLemmaTokens());
    assertEquals(expected.getTextTokens(), actual.getTextTokens());
  }

}
