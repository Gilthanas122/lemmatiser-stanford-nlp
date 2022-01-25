package com.example.stanfordnlpgerman.services.newsarticleservice;

import com.example.stanfordnlpgerman.models.dtos.newsarticle.CreateNewsPaperArticleDTO;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.MostRelevantNewsArticleDTOAGG;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.NewsArticleDataDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NewsArticleService {

  void saveArticle(CreateNewsPaperArticleDTO createNewsPaperArticleDTO) throws Exception;

  NewsArticleDataDTO findByNewsArticleId(long newsArticleId);

  List<MostRelevantNewsArticleDTOAGG> getMostRelevantNewsArticles(int pageNumber);
}
