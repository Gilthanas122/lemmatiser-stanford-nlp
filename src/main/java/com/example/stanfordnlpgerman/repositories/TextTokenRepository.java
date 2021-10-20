package com.example.stanfordnlpgerman.repositories;

import com.example.stanfordnlpgerman.models.dao.TextToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextTokenRepository extends JpaRepository<TextToken, Long> {
}
