package com.example.stanfordnlpgerman.repositories;

import com.example.stanfordnlpgerman.models.dao.LemmaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LemmaTypeRepository extends JpaRepository<LemmaType, Long> {
}
