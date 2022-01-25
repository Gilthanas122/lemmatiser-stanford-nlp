package com.example.stanfordnlpgerman.services.newsarticleservice;

import com.example.stanfordnlpgerman.models.dao.NewsArticle;
import com.example.stanfordnlpgerman.models.dao.Sentence;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.CreateNewsPaperArticleDTO;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.MostRelevantNewsArticleDTOAGG;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.MostRelevantNewsArticlesDTO;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.NewsArticleDataDTO;
import com.example.stanfordnlpgerman.repositories.NewsArticleRepository;
import com.example.stanfordnlpgerman.services.validations.ErrorServiceImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
  public void saveArticle(CreateNewsPaperArticleDTO createNewsPaperArticleDTO) throws Exception {
    ErrorServiceImpl.buildMissingFieldErrorMessage(createNewsPaperArticleDTO);
    newsArticleAsyncService.createNewsPaperArticle(createNewsPaperArticleDTO);
  }

  @Override
  public NewsArticleDataDTO findByNewsArticleId(long newsArticleId) {
    NewsArticle newsArticle = newsArticleRepository.findNewsArticleBySentenceId(newsArticleId);
    if (newsArticle != null) {
      return NewsArticleDataDTO.builder()
              .newsPaperName(newsArticle.getNewsPaperName())
              .title(newsArticle.getTitle())
              .publicationYear(newsArticle.getPublicationYear())
              .text(newsArticle.getSentences().stream().map(Sentence::getText).collect(Collectors.joining()))
              .build();
    }
    return new NewsArticleDataDTO();
  }

  @Override
  public List<MostRelevantNewsArticleDTOAGG> getMostRelevantNewsArticles(int pageNumber) {
    List<MostRelevantNewsArticlesDTO> mostRelevantNewsArticlesDTOS = newsArticleRepository.findMostRelevantNewsArticles(PageRequest.of(pageNumber, 25, Sort.by("relevance")));
    List<MostRelevantNewsArticleDTOAGG> mostRelevantNewsArticleDTOAGGS = new ArrayList<>();
    mostRelevantNewsArticleDTOAGGS.add(new MostRelevantNewsArticleDTOAGG(mostRelevantNewsArticlesDTOS.get(0)));
    int position = 0;
    for (MostRelevantNewsArticlesDTO m: mostRelevantNewsArticlesDTOS) {
      if (m.getId() != mostRelevantNewsArticleDTOAGGS.get(position).getId()){
        mostRelevantNewsArticleDTOAGGS.add(new MostRelevantNewsArticleDTOAGG(m));
        position++;
      }
      String newText = mostRelevantNewsArticleDTOAGGS.get(position).getText().concat(" " + m.getText());
      mostRelevantNewsArticleDTOAGGS.get(position).setText(newText);
    }
    return mostRelevantNewsArticleDTOAGGS;
  }
}
