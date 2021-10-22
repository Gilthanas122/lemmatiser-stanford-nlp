package com.example.stanfordnlpgerman.repositories;

import com.example.stanfordnlpgerman.models.dao.LemmaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface LemmaTypeRepository extends JpaRepository<LemmaType, Long> {

  @Query("SELECT lt FROM LemmaToken lto JOIN FETCH LemmaType lt ON lto.lemmaType.id = lt.id " +
          "WHERE lto.text = ?1 OR lt.text = ?1")
  Set<LemmaType> findAllByText(String originalText);
}
