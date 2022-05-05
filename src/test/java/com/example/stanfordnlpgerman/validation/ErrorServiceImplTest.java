package com.example.stanfordnlpgerman.validation;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.example.stanfordnlpgerman.exceptions.validations.MissingParamsException;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.CreateNewsPaperArticleDTO;
import com.example.stanfordnlpgerman.services.validations.ErrorServiceImpl;
import com.example.stanfordnlpgerman.testobjectcreation.CreateNewsPaperArticleDTOCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ErrorServiceImplTest {
  private final Logger logger = (Logger) LoggerFactory.getLogger(ErrorServiceImpl.class);
  private final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();

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
  void buildMissingFieldErrorMessage_noError_shouldLogNothing() {
    List<ILoggingEvent> logsList = listAppender.list;
    CreateNewsPaperArticleDTO createNewsPaperArticleDTO = CreateNewsPaperArticleDTOCreator.createCreateNewsPaperArticleDTO();

    ErrorServiceImpl.buildMissingFieldErrorMessage(createNewsPaperArticleDTO);

    assertTrue(logsList.isEmpty());
  }

  @Test
  void buildMissingFieldErrorMessage_withTwoMissingFields_shouldLogAndThrowMissingParamsException() {
    CreateNewsPaperArticleDTO createNewsPaperArticleDTO = CreateNewsPaperArticleDTOCreator.createCreateNewsPaperArticleDTO();
    createNewsPaperArticleDTO.setNewsPaperName(null);
    createNewsPaperArticleDTO.setTitle(null);

    List<ILoggingEvent> logsList = listAppender.list;

    MissingParamsException exception = assertThrows(MissingParamsException.class, () -> ErrorServiceImpl.buildMissingFieldErrorMessage(createNewsPaperArticleDTO));

    assertEquals(1, logsList.size());
    assertEquals("Title, newsPaperName is required.", logsList.get(0).getFormattedMessage());
    assertEquals("Title, newsPaperName is required.", exception.getMessage());

  }

  @Test
  void buildMissingFieldErrorMessag_inputNull_shouldThrowMissingParamsException(){
    List<ILoggingEvent> logsList = listAppender.list;

    MissingParamsException exception = assertThrows(MissingParamsException.class, () -> ErrorServiceImpl.buildMissingFieldErrorMessage(null));

    assertEquals(1, logsList.size());
    assertEquals("Object to be verified for null or empty fields is null", logsList.get(0).getFormattedMessage());
    assertEquals("Object to be verified for null or empty fields is null", exception.getMessage());
  }
}
