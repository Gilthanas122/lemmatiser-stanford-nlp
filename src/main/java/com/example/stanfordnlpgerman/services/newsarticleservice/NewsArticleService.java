package com.example.stanfordnlpgerman.services.newsarticleservice;

import com.example.stanfordnlpgerman.exceptions.lemmatokens.LemmaTokenNotFoundByTextException;
import com.example.stanfordnlpgerman.exceptions.lemmatokens.MoreThanOneLemmaTokenBelongingToTheText;
import com.example.stanfordnlpgerman.exceptions.validations.MissingParamsException;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.CreateNewsPaperArticleDTO;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.NewsArticleDataDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NewsArticleService {

  void saveArticle(CreateNewsPaperArticleDTO createNewsPaperArticleDTO) throws MissingParamsException, LemmaTokenNotFoundByTextException, MoreThanOneLemmaTokenBelongingToTheText;

  NewsArticleDataDTO findNewsPaperBySentenceId(long newsArticleId);
}
