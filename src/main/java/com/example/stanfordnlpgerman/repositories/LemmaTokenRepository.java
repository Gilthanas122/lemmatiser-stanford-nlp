package com.example.stanfordnlpgerman.repositories;

import com.example.stanfordnlpgerman.models.dao.LemmaToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LemmaTokenRepository extends JpaRepository<LemmaToken, Long> {
}
