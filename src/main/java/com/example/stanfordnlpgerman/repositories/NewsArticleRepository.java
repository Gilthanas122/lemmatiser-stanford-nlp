package com.example.stanfordnlpgerman.repositories;

import com.example.stanfordnlpgerman.models.dao.NewsArticle;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.NewsArticleDataDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {


  @Query("SELECT na from NewsArticle na where na.id = ?1")
  NewsArticle findNewsArticleBySentenceId(long newsArticleId);
/*
  SELECT col1, col2, ..., colN
  GROUP_CONCAT ( [DISTINCT] col_name1
                  [ORDER BY clause]  [SEPARATOR str_val] )
  FROM table_name GROUP BY col_name2;*/
}
