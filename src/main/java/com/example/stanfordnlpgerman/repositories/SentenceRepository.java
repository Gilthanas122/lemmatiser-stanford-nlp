package com.example.stanfordnlpgerman.repositories;

import com.example.stanfordnlpgerman.models.dao.Sentence;
import com.example.stanfordnlpgerman.models.dtos.sentence.SentenceTextAndNewsPaperIdDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {
  @Query("SELECT DISTINCT s.text as sentenceText, s.id as sentenceId FROM Sentence s JOIN s.lemmaTypes lt WHERE lt.id = ?1")
  List<SentenceTextAndNewsPaperIdDTO> findByLemmaTypeId(long lemmaTypeId);
}
