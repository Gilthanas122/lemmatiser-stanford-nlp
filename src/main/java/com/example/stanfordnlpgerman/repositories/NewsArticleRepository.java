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

  @Query("SELECT DISTINCT na.newsPaperName AS newsPaperName, na.publicationYear AS publicationYear, na.id AS id, s.text as text FROM NewsArticle na JOIN na.sentences s ORDER BY na.relevance, na.publicationYear")
  List<MostRelevantNewsArticlesDTO> findMostRelevantNewsArticles(PageRequest relevance);

  @Query(value = "select " +
          " temp.news_paper_name as newspapername " +
          ",temp.publication_year as publicationyear " +
          ",temp.id as id " +
          ",group_concat(distinct temp.text " +
          "     order by temp.text_position " +
          " separator ' ') as text " +
          "from " +
          "(" +
          " select" +
          " newsarticles.news_paper_name " +
          ", newsarticles.publication_year" +
          ", newsarticles.id" +
          ", sentences.text" +
          ", sentences.text_position" +
          ", newsarticles.relevance" +
          ", sentences.news_article_id" +
          " from newsarticles " +
          "left join sentences on newsarticles.id = sentences.news_article_id " +
          ") temp" +
          " group by temp.news_article_id;", nativeQuery = true)
  List<MostRelevantNewsArticlesDTO> findMostRelevantNewsArticles2(PageRequest relevance);
}
