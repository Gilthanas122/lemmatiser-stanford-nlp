package com.example.stanfordnlpgerman.repositories;

import com.example.stanfordnlpgerman.models.dao.NewsArticle;
import com.example.stanfordnlpgerman.models.dao.Sentence;
import com.example.stanfordnlpgerman.models.dao.TextToken;
import com.example.stanfordnlpgerman.models.dtos.sentence.InvalidSentencesDTO;
import com.example.stanfordnlpgerman.models.dtos.sentence.SentenceTextAndNewsPaperIdDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {
  @Query("SELECT DISTINCT s.text as sentenceText, s.newsArticle.id as newsArticleId" +
          " FROM Sentence s JOIN s.lemmaTypes lt WHERE lt.id = ?1")
  List<SentenceTextAndNewsPaperIdDTO> findAllByLemmaTypeId(long lemmaTypeId);

  @Query("SELECT tt FROM Sentence s JOIN s.lemmaTypes lt JOIN s.textTokens tt WHERE lt.id = ?1")
  List<TextToken> sentencesContainingLemma(long lemmaTypeId);

  @Query("SELECT s.id FROM Sentence s JOIN s.lemmaTypes lt JOIN s.textTokens tt WHERE lt.id = ?1")
  List<Long> sentenceIdsContainingLemma(long lemmaTypeId);

  @Query("SELECT s.text AS text, s.id AS id FROM Sentence s WHERE s.invalid = true")
  List<InvalidSentencesDTO> getInvalidSentences();

  @Query("SELECT n FROM NewsArticle n JOIN n.sentences s WHERE s.id = ?1")
  NewsArticle findNewsArticleBySentenceId(long id);

  @Transactional
  @Modifying
  @Query("UPDATE Sentence s SET s.invalid = false WHERE s.id = ?1")
  void makeSentenceValidById(long id);

}
