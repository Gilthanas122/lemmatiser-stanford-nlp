package com.example.stanfordnlpgerman.repositories;

import com.example.stanfordnlpgerman.models.dao.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {
}
