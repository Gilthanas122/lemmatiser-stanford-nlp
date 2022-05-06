package com.example.stanfordnlpgerman.newsarticle;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.example.stanfordnlpgerman.models.dao.NewsArticle;
import com.example.stanfordnlpgerman.models.dao.Sentence;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.CreateNewsPaperArticleDTO;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.MostRelevantNewsArticleDTOAGG;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.MostRelevantNewsArticlesDTO;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.NewsArticleDataDTO;
import com.example.stanfordnlpgerman.repositories.NewsArticleRepository;
import com.example.stanfordnlpgerman.services.newsarticleservice.NewsArticleAsyncService;
import com.example.stanfordnlpgerman.services.newsarticleservice.NewsArticleException;
import com.example.stanfordnlpgerman.services.newsarticleservice.NewsArticleServiceImpl;
import com.example.stanfordnlpgerman.testobjectcreation.CreateNewsPaperArticleDTOCreator;
import com.example.stanfordnlpgerman.testobjectcreation.NewsArticleCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NewsArticleServiceTest {
    private final Logger logger = (Logger) LoggerFactory.getLogger(NewsArticleServiceImpl.class);
    private final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    @Mock
    private NewsArticleAsyncService newsArticleAsyncService;
    @Mock
    private NewsArticleRepository newsArticleRepository;
    @InjectMocks
    private NewsArticleServiceImpl newsArticleService;

    @BeforeEach
    void setup() {
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
    }

    @Test
    void saveNewsArticle_success() {
        CreateNewsPaperArticleDTO createNewsPaperArticleDTO = CreateNewsPaperArticleDTOCreator.createCreateNewsPaperArticleDTO(1);
        List<ILoggingEvent> logsList = listAppender.list;

        newsArticleService.saveNewsArticle(createNewsPaperArticleDTO);

        assertEquals(1, logsList.size());
        assertEquals("NewsArticle save with title: my title1", logsList.get(0).getFormattedMessage());
        verify(newsArticleAsyncService, times(1)).saveNewsPaperArticle(createNewsPaperArticleDTO);
    }

    @Test
    void saveNewsArticle_withNullCreateNewsPaperArticleDTO_shouldThrowNewsArticleException() {
        CreateNewsPaperArticleDTO createNewsPaperArticleDTO = null;

        NewsArticleException exception = assertThrows(NewsArticleException.class, () -> newsArticleService.saveNewsArticle(createNewsPaperArticleDTO));
        List<ILoggingEvent> logsList = listAppender.list;

        assertEquals(1, logsList.size());
        assertEquals("Newarticle to be saved can not be null", logsList.get(0).getFormattedMessage());
        assertEquals("Newarticle to be saved can not be null", exception.getMessage());
    }

    @Test
    void findNewsArticleById_success_shouldReturnNewsArticleDataDTO() {
        NewsArticle newsArticle = NewsArticleCreator.createNewsArticle(1);
        NewsArticleDataDTO expectedewsArticleDataDTO =
                NewsArticleDataDTO.builder()
                        .newsPaperName(newsArticle.getNewsPaperName())
                        .publicationYear(newsArticle.getPublicationYear())
                        .text(newsArticle.getSentences().stream().map(Sentence::getText).collect(Collectors.joining()))
                        .title(newsArticle.getTitle())
                        .build();

        when(newsArticleRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));
        List<ILoggingEvent> logsList = listAppender.list;

        NewsArticleDataDTO actualNewsArticleDataDTO = newsArticleService.findNewsArticleById(newsArticle.getId());

        assertEquals(1, logsList.size());
        assertEquals("NewsArticle found by id : 1", logsList.get(0).getFormattedMessage());
        assertEquals(expectedewsArticleDataDTO.getAuthor(), actualNewsArticleDataDTO.getAuthor());
        assertEquals(expectedewsArticleDataDTO.getNewsPaperName(), actualNewsArticleDataDTO.getNewsPaperName());
        assertEquals(expectedewsArticleDataDTO.getText(), actualNewsArticleDataDTO.getText());
        assertEquals(expectedewsArticleDataDTO.getPublicationYear(), actualNewsArticleDataDTO.getPublicationYear());
    }

    @Test
    void findNewsArticleById_noNewsPaperFoundById_shouldReturnNewNewsArticleDataDTO() {
        NewsArticle newsArticle = NewsArticleCreator.createNewsArticle(1);

        when(newsArticleRepository.findById(newsArticle.getId())).thenReturn(Optional.empty());
        List<ILoggingEvent> logsList = listAppender.list;

        NewsArticleDataDTO actualNewsArticleDataDTO = newsArticleService.findNewsArticleById(newsArticle.getId());

        assertEquals(1, logsList.size());
        assertEquals("NewsArticle was not found by id : 1", logsList.get(0).getFormattedMessage());
        assertNull(actualNewsArticleDataDTO.getAuthor());
        assertNull(actualNewsArticleDataDTO.getNewsPaperName());
        assertNull(actualNewsArticleDataDTO.getText());
        assertEquals(0, actualNewsArticleDataDTO.getPublicationYear());
    }

    @Test
    void getMostRelevantNewsArticles_success_shouldReturnMostRelevantNewsArticleDTOAGGList() {
        short pageNumber = 1;
        List<MostRelevantNewsArticlesDTO> mostRelevantNewsArticlesDTOS = createMostRelevantNewsArticleDTOs(2);
        List<MostRelevantNewsArticleDTOAGG> expectedMostRelevantNewsArticleDTOAGGS = mostRelevantNewsArticlesDTOS.stream().map(MostRelevantNewsArticleDTOAGG::new).toList();

        when(newsArticleRepository.findMostRelevantNewsArticles(PageRequest.of(pageNumber, 25, Sort.by("relevance")))).thenReturn(mostRelevantNewsArticlesDTOS);
        List<ILoggingEvent> logsList = listAppender.list;

        List<MostRelevantNewsArticleDTOAGG> actualMostRelevantNewsArticleDTOAGGS = newsArticleService.getMostRelevantNewsArticles(pageNumber);

        assertEquals(1, logsList.size());
        assertEquals("2 most relevant articles were returned ", logsList.get(0).getFormattedMessage());
        assertEquals(expectedMostRelevantNewsArticleDTOAGGS.size(), actualMostRelevantNewsArticleDTOAGGS.size());
        assertMostRelevantAGGS(expectedMostRelevantNewsArticleDTOAGGS.get(0), actualMostRelevantNewsArticleDTOAGGS.get(0));
        assertMostRelevantAGGS(expectedMostRelevantNewsArticleDTOAGGS.get(1), actualMostRelevantNewsArticleDTOAGGS.get(1));
    }

    @Test
    void getMostRelevantNewsArticles_withSameId_shouldReturnMostRelevantNewsArticleDTOAGGListWithOneItem() {
        short pageNumber = 1;
        List<MostRelevantNewsArticlesDTO> mostRelevantNewsArticlesDTOS = createMostRelevantNewsArticleDTOs(2);
        mostRelevantNewsArticlesDTOS.remove(1);
        List<MostRelevantNewsArticleDTOAGG> expectedMostRelevantNewsArticleDTOAGGS = List.of(new MostRelevantNewsArticleDTOAGG(mostRelevantNewsArticlesDTOS.get(0)));

        when(newsArticleRepository.findMostRelevantNewsArticles(PageRequest.of(pageNumber, 25, Sort.by("relevance")))).thenReturn(mostRelevantNewsArticlesDTOS);
        List<ILoggingEvent> logsList = listAppender.list;

        List<MostRelevantNewsArticleDTOAGG> actualMostRelevantNewsArticleDTOAGGS = newsArticleService.getMostRelevantNewsArticles(pageNumber);

        assertEquals(1, logsList.size());
        assertEquals("1 most relevant articles were returned ", logsList.get(0).getFormattedMessage());
        assertEquals(expectedMostRelevantNewsArticleDTOAGGS.size(), actualMostRelevantNewsArticleDTOAGGS.size());
        assertMostRelevantAGGS(expectedMostRelevantNewsArticleDTOAGGS.get(0), actualMostRelevantNewsArticleDTOAGGS.get(0));
    }

    @Test
    void getMostRelevantNewsArticles_withPageNumberBelow0_shouldThrowNewsArticleException() {
        short pageNumber = -1;

        NewsArticleException exception = assertThrows(NewsArticleException.class, () -> newsArticleService.getMostRelevantNewsArticles(pageNumber));
        List<ILoggingEvent> logsList = listAppender.list;


        assertEquals(1, logsList.size());
        assertEquals("Pagenumber can not be below 0", logsList.get(0).getFormattedMessage());
        assertEquals("Pagenumber can not be below 0", exception.getMessage());
    }

    @Test
    void startReading_success_shouldCallNewsArticleAsynchServiceWithTwoFiles() {
        String dir = "src/test/resources/goodfiles";
        CreateNewsPaperArticleDTO expectedCreateNewsPaperArticleDTO = createNewsArticleDTOs();

        newsArticleService.readFiles(dir);

        List<ILoggingEvent> logsList = listAppender.list;

        ArgumentCaptor<CreateNewsPaperArticleDTO> argumentCaptor = ArgumentCaptor.forClass(CreateNewsPaperArticleDTO.class);
        verify(newsArticleAsyncService, times(2)).saveNewsPaperArticle(argumentCaptor.capture());
        CreateNewsPaperArticleDTO actualCreateNewsPaperArticleDTOs = argumentCaptor.getValue();

        assertEquals(2, logsList.size());
        assertEquals("File is not a regular file. Path: " + dir, logsList.get(0).getFormattedMessage());
        assertEquals("2 file(s) was/were read from folder " + dir, logsList.get(1).getFormattedMessage());
        verify(newsArticleAsyncService, times(2)).saveNewsPaperArticle(any());

        assertCreateNewsPaperArticleDTO(expectedCreateNewsPaperArticleDTO, actualCreateNewsPaperArticleDTOs);
    }

    @Test
    void startReading_withOneBadExtensionFile_shouldCallNewsArticleAsynchServiceWithOneFile() {
        String dir = "src/test/resources/badextensionfile";
        CreateNewsPaperArticleDTO expectedCreateNewsPaperArticleDTO = createNewsArticleDTOs();

        newsArticleService.readFiles(dir);

        List<ILoggingEvent> logsList = listAppender.list;

        ArgumentCaptor<CreateNewsPaperArticleDTO> argumentCaptor = ArgumentCaptor.forClass(CreateNewsPaperArticleDTO.class);
        verify(newsArticleAsyncService).saveNewsPaperArticle(argumentCaptor.capture());
        CreateNewsPaperArticleDTO actualCreateNewsPaperArticleDTO = argumentCaptor.getValue();

        assertEquals(3, logsList.size());
        assertEquals("File is not a regular file. Path: " + dir, logsList.get(0).getFormattedMessage());
        assertEquals(String.format("Not a txt file on path: %s/mypython.py", dir), logsList.get(1).getFormattedMessage());
        assertEquals("1 file(s) was/were read from folder " + dir, logsList.get(2).getFormattedMessage());

        verify(newsArticleAsyncService, times(1)).saveNewsPaperArticle(any());
    }

    @Test
    void startReading_withInvalidPath_shouldThrowNewsArticleException() {
        String dir = "src/test/resources/badpath";

        NewsArticleException exception = assertThrows(NewsArticleException.class, () -> newsArticleService.readFiles(dir));
        List<ILoggingEvent> logsList = listAppender.list;

        assertEquals(1, logsList.size());
        assertEquals("Could not read directory or file from path: " + dir, logsList.get(0).getFormattedMessage());
        assertEquals("Could not read directory or file from path: " + dir, exception.getMessage());
    }

    @Test
    void startReading_withOneLineShorterThan4Lines_shouldCallNewsArticleAsynchServiceWithOneFile() {
        String dir = "src/test/resources/shorter";

        newsArticleService.readFiles(dir);
        List<ILoggingEvent> logsList = listAppender.list;

        assertEquals(3, logsList.size());
        assertEquals(String.format("File is not a regular file. Path: %s", dir), logsList.get(0).getFormattedMessage());
        assertEquals(String.format("An article can not be shorter than 4 lines. Path: %s/shorterthan4lines.txt", dir), logsList.get(1).getFormattedMessage());
        assertEquals("1 file(s) was/were read from folder " + dir, logsList.get(2).getFormattedMessage());
    }

    private void assertCreateNewsPaperArticleDTO(CreateNewsPaperArticleDTO expected, CreateNewsPaperArticleDTO actual) {
        assertEquals(expected.getNewsPaperName(), actual.getNewsPaperName());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getPublicationYear(), actual.getPublicationYear());
        assertEquals(expected.getText(), actual.getText());
    }

    private CreateNewsPaperArticleDTO createNewsArticleDTOs() {
        return CreateNewsPaperArticleDTO.builder()
                .newsPaperName("News Name 2")
                .publicationYear(1999)
                .title("News Title 2")
                .text("my text 2")
                .build();
    }

    private void assertMostRelevantAGGS(MostRelevantNewsArticleDTOAGG expected, MostRelevantNewsArticleDTOAGG actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getText(), actual.getText());
        assertEquals(expected.getNewsPaperName(), actual.getNewsPaperName());
        assertEquals(expected.getPublicationDate(), actual.getPublicationDate());
    }

    private List<MostRelevantNewsArticlesDTO> createMostRelevantNewsArticleDTOs(int amount) {
        List<MostRelevantNewsArticlesDTO> mostRelevantNewsArticleDTOs = new ArrayList<>();
        for (int index = 1; index <= amount; index++) {
            mostRelevantNewsArticleDTOs.add(createMostRelevantNewsArticle(index));
        }
        return mostRelevantNewsArticleDTOs;
    }

    private MostRelevantNewsArticlesDTO createMostRelevantNewsArticle(int index) {
        return new MostRelevantNewsArticlesDTO() {
            @Override
            public long getId() {
                return index;
            }

            @Override
            public String getNewsPaperName() {
                return "most relevant news paper name " + index;
            }

            @Override
            public String getText() {
                return "most relevant text " + index;
            }

            @Override
            public LocalDate getPublicationDate() {
                return LocalDate.of(1999 + index, Month.APRIL, 1);
            }
        };
    }
}
