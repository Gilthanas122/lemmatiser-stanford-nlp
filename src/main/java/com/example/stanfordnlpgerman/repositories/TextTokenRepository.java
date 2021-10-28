package com.example.stanfordnlpgerman.repositories;

import com.example.stanfordnlpgerman.models.dao.TextToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TextTokenRepository extends JpaRepository<TextToken, Long> {

  @Query("SELECT tt from TextToken tt where tt.invalid = true")
  Set<TextToken> getTextTokensInvalid();


  TextToken findById(long id);
}
