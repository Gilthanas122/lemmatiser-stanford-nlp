package com.example.stanfordnlpgerman.services.newsarticleservice;

import com.example.stanfordnlpgerman.exceptions.lemmatokens.LemmaTokenNotFoundByTextException;
import com.example.stanfordnlpgerman.exceptions.lemmatokens.MoreThanOneLemmaTokenBelongingToTheText;
import com.example.stanfordnlpgerman.exceptions.validations.MissingParamsException;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.CreateNewsPaperArticleDTO;
import org.springframework.stereotype.Service;

@Service
public interface NewsArticleService {

  void saveArticle(CreateNewsPaperArticleDTO createNewsPaperArticleDTO) throws MissingParamsException, LemmaTokenNotFoundByTextException, MoreThanOneLemmaTokenBelongingToTheText;
}
