package com.example.stanfordnlpgerman.services.newsarticleservice;

import com.example.stanfordnlpgerman.models.dtos.newsarticle.CreateNewsPaperArticleDTO;
import org.springframework.stereotype.Service;

@Service
public interface NewsArticleAsyncService {
  void saveNewsPaperArticle(CreateNewsPaperArticleDTO createNewsPaperArticleDTOs);

}
