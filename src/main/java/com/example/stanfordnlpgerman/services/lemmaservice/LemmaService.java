package com.example.stanfordnlpgerman.services.lemmaservice;

import com.example.stanfordnlpgerman.models.dtos.CreateNewsPaperArticleDTO;
import org.springframework.stereotype.Service;

@Service
public interface LemmaService {

  void saveArticle(CreateNewsPaperArticleDTO createNewsPaperArticleDTO);
}
