package com.example.stanfordnlpgerman.services.newsarticleservice;

import com.example.stanfordnlpgerman.models.dao.NewsArticle;
import com.example.stanfordnlpgerman.models.dao.Sentence;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.CreateNewsPaperArticleDTO;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.MostRelevantNewsArticleDTOAGG;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.MostRelevantNewsArticlesDTO;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.NewsArticleDataDTO;
import com.example.stanfordnlpgerman.repositories.NewsArticleRepository;
import com.example.stanfordnlpgerman.services.validations.ErrorServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class NewsArticleServiceImpl implements NewsArticleService {
  private final NewsArticleAsyncService newsArticleAsyncService;
  private final NewsArticleRepository newsArticleRepository;

  public NewsArticleServiceImpl(NewsArticleAsyncService newsArticleAsyncService, NewsArticleRepository newsArticleRepository) {
    this.newsArticleAsyncService = newsArticleAsyncService;
    this.newsArticleRepository = newsArticleRepository;
  }

  @Override
  public void saveNewsArticle(CreateNewsPaperArticleDTO createNewsPaperArticleDTO) {
    if (createNewsPaperArticleDTO == null) {
      log.error("Newarticle to be saved can not be null");
      throw new NewsArticleException("Newarticle to be saved can not be null");
    }
    ErrorServiceImpl.buildMissingFieldErrorMessage(createNewsPaperArticleDTO);
    log.info("NewsArticle save with title: {}", createNewsPaperArticleDTO.getTitle());
    newsArticleAsyncService.saveNewsPaperArticle(createNewsPaperArticleDTO);
  }

  @Override
  public NewsArticleDataDTO findNewsArticleById(long newsArticleId) {
    NewsArticle newsArticle = newsArticleRepository.findById(newsArticleId).orElse(null);
    if (newsArticle != null) {
      log.info("NewsArticle found by id : {}", newsArticleId);
      return NewsArticleDataDTO.builder()
              .newsPaperName(newsArticle.getNewsPaperName())
              .title(newsArticle.getTitle())
              .publicationYear(newsArticle.getPublicationYear())
              .text(newsArticle.getSentences().stream().map(Sentence::getText).collect(Collectors.joining()))
              .build();
    }
    log.info("NewsArticle was not found by id : {}", newsArticleId);
    return new NewsArticleDataDTO();
  }

  @Override
  public List<MostRelevantNewsArticleDTOAGG> getMostRelevantNewsArticles(int pageNumber) {
    if (pageNumber < 0) {
      log.error("Pagenumber can not be below 0");
      throw new NewsArticleException("Pagenumber can not be below 0");
    }
    List<MostRelevantNewsArticlesDTO> mostRelevantNewsArticlesDTOS = newsArticleRepository.findMostRelevantNewsArticles(PageRequest.of(pageNumber, 25, Sort.by("relevance")));
    List<MostRelevantNewsArticleDTOAGG> mostRelevantNewsArticleDTOAGGS = new ArrayList<>();
    AtomicInteger position = new AtomicInteger();
    mostRelevantNewsArticlesDTOS.forEach(mostRelevant -> {
      if (!mostRelevantNewsArticleDTOAGGS.isEmpty()) {
        if (mostRelevant.getId() != mostRelevantNewsArticleDTOAGGS.get(position.get()).getId()) {
          mostRelevantNewsArticleDTOAGGS.add(new MostRelevantNewsArticleDTOAGG(mostRelevant));
          position.getAndIncrement();
        } else {
          String newText = mostRelevantNewsArticleDTOAGGS.get(position.get()).getText().concat(" " + mostRelevant.getText());
          mostRelevantNewsArticleDTOAGGS.get(position.get()).setText(newText);
        }
      } else {
        mostRelevantNewsArticleDTOAGGS.add(new MostRelevantNewsArticleDTOAGG(mostRelevant));
      }
    });
    log.info("{} most relevant articles were returned ", mostRelevantNewsArticleDTOAGGS.size());

    return mostRelevantNewsArticleDTOAGGS;
  }

  @Override
  public void readFiles(String dir) {
    List<CreateNewsPaperArticleDTO> createNewsPaperArticleDTOs = new ArrayList<>();
    List<File> files = readFilesFromDirectory(dir);
    files.forEach(file -> {
      CreateNewsPaperArticleDTO createNewsPaperArticleDTO = new CreateNewsPaperArticleDTO();
      List<String> lines;
      try {
        lines = Files.readAllLines(Paths.get(file.getPath()));
        if (lines.size() < 4){
          log.error("An article can not be shorter than 4 lines. Path: {}", file.getPath());
          return;
        }
        createNewsPaperArticleDTO.setNewsPaperName(lines.get(0));
        createNewsPaperArticleDTO.setPublicationYear(Integer.parseInt(lines.get(1).trim()));
        createNewsPaperArticleDTO.setTitle(lines.get(2));
        StringBuilder fileText = new StringBuilder();
        fileText.append(String.join(" ", lines.subList(3, lines.size())));
        createNewsPaperArticleDTO.setText(fileText.toString());
        createNewsPaperArticleDTOs.add(createNewsPaperArticleDTO);

      } catch (IOException e) {
        log.error("Could not read directory or file");
        throw new NewsArticleException("Could not read directory or file");
      }
    });

    log.info("{} file(s) was/were read from folder {}", createNewsPaperArticleDTOs.size(), dir);
    createNewsPaperArticleDTOs.forEach(newsArticleAsyncService::saveNewsPaperArticle);
  }

  private List<File> readFilesFromDirectory(String dir) {
    List<File> files = new ArrayList<>();
    try (Stream<Path> paths = Files.walk(Paths.get(dir))) {
      paths
              .forEach(path -> {
                if (!Files.isRegularFile(path)) {
                  log.error("File is not a regular file. Path: {}", path);
                } else if (!isTxtFile(path)) {
                  log.error("Not a txt file on path: {}", path);
                } else {
                  files.add(new File(String.valueOf(path)));
                }
              });
    } catch (IOException e) {
      log.error("Could not read directory or file from path: " + dir);
      throw new NewsArticleException("Could not read directory or file from path: " + dir);
    }
    return files;
  }

  private boolean isTxtFile(Path path) {
    int indexOfExtension = path.toString().lastIndexOf(".") + 1;
    return path.toString()
            .substring(indexOfExtension)
            .equals("txt");
  }

}
