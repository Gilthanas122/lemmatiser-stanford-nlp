package com.example.stanfordnlpgerman.services.newsarticleservice;

import com.example.stanfordnlpgerman.models.dtos.newsarticle.CreateNewsPaperArticleDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NewsArticleAsyncService {
  void saveNewsPaperArticle(CreateNewsPaperArticleDTO createNewsPaperArticleDTOs);

  void saveNewsPaperArticles(List<CreateNewsPaperArticleDTO> createNewsPaperArticleDTOS);

}
