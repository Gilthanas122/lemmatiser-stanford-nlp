package com.example.stanfordnlpgerman.services.newsarticleservice;

import com.example.stanfordnlpgerman.models.dtos.CreateNewsPaperArticleDTO;
import org.springframework.stereotype.Service;

@Service
public interface NewsArticleAsyncService {
  void createNewsPaperArticle(CreateNewsPaperArticleDTO createNewsPaperArticleDTO);
}
