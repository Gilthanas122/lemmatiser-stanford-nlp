package com.example.stanfordnlpgerman.repositories;

import com.example.stanfordnlpgerman.models.dao.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
}
