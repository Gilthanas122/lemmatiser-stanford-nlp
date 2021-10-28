package com.example.stanfordnlpgerman.services.newsarticleservice;

import com.example.stanfordnlpgerman.exceptions.validations.MissingParamsException;
import com.example.stanfordnlpgerman.models.dao.NewsArticle;
import com.example.stanfordnlpgerman.models.dao.Sentence;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.CreateNewsPaperArticleDTO;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.NewsArticleDataDTO;
import com.example.stanfordnlpgerman.repositories.NewsArticleRepository;
import com.example.stanfordnlpgerman.services.validations.ErrorServiceImpl;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class NewsArticleServiceImpl implements NewsArticleService {
  private final NewsArticleAsyncService newsArticleAsyncService;
  private final NewsArticleRepository newsArticleRepository;

  public NewsArticleServiceImpl(NewsArticleAsyncService newsArticleAsyncService, NewsArticleRepository newsArticleRepository) {
    this.newsArticleAsyncService = newsArticleAsyncService;
    this.newsArticleRepository = newsArticleRepository;
  }

  @Override
  public void saveArticle(CreateNewsPaperArticleDTO createNewsPaperArticleDTO) throws MissingParamsException {
    ErrorServiceImpl.buildMissingFieldErrorMessage(createNewsPaperArticleDTO);
    if (createNewsPaperArticleDTO.getPageNumber() < 1) {
      throw new MissingParamsException("Following parameters are missing: pageNumber");
    }
    newsArticleAsyncService.createNewsPaperArticle(createNewsPaperArticleDTO);
  }

  @Override
  public NewsArticleDataDTO findByNewsArticleId(long newsArticleId) {
    NewsArticle newsArticle = newsArticleRepository.findNewsArticleBySentenceId(newsArticleId);
    if (newsArticle != null) {
      return NewsArticleDataDTO.builder()
              .newsPaperName(newsArticle.getNewsPaperName())
              .author(newsArticle.getAuthor())
              .pageNumber(newsArticle.getPageNumber())
              .title(newsArticle.getTitle())
              .date(newsArticle.getPublicationDate())
              .text(newsArticle.getSentences().stream().map(Sentence::getText).collect(Collectors.joining()))
              .build();
    }
    return new NewsArticleDataDTO();
  }
}
