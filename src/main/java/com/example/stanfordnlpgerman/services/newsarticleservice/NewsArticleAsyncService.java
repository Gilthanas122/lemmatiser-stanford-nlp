package com.example.stanfordnlpgerman.services.newsarticleservice;

import com.example.stanfordnlpgerman.models.dao.LemmaToken;
import com.example.stanfordnlpgerman.models.dao.LemmaType;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.CreateNewsPaperArticleDTO;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface NewsArticleAsyncService {
  void createNewsPaperArticle(CreateNewsPaperArticleDTO createNewsPaperArticleDTO) throws Exception;

}
