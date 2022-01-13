package com.example.stanfordnlpgerman.repositories;

import com.example.stanfordnlpgerman.models.dao.NewsArticle;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.MostRelevantNewsArticlesDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {

  @Query("SELECT na from NewsArticle na where na.id = ?1")
  NewsArticle findNewsArticleBySentenceId(long newsArticleId);

  @Query("SELECT DISTINCT na.newsPaperName AS newsPaperName, na.publicationDate AS publicationDate, na.id AS id, s.text as text FROM NewsArticle na JOIN na.sentences s ORDER BY na.relevance, na.publicationDate")
  List<MostRelevantNewsArticlesDTO> findMostRelevantNewsArticles(PageRequest relevance);

/*  @Query(value = "SELECT newsarticles.news_paper_name AS newsPaperName, newsarticles.publication_date AS publicationDate, newsarticles.id AS id, group_concat(sentences.text) AS text FROM newsarticles JOIN sentences ON newsarticles.id = sentences.news_article_id ORDER BY newsarticles.relevance", nativeQuery = true)
  List<MostRelevantNewsArticlesDTO> findMostRelevantNewsArticles(PageRequest relevance);*/
}
