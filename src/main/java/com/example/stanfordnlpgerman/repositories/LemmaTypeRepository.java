package com.example.stanfordnlpgerman.repositories;

import com.example.stanfordnlpgerman.models.dao.LemmaType;
import com.example.stanfordnlpgerman.models.dtos.lemmatype.ShowMostCommonLemmasDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface LemmaTypeRepository extends JpaRepository<LemmaType, Long>, PagingAndSortingRepository<LemmaType,Long> {

  @Query("SELECT lt FROM LemmaToken lto JOIN LemmaType lt ON lto.lemmaType.id = lt.id " +
          "WHERE lto.text = ?1 OR lt.text = ?1")
  Set<LemmaType> findAllByText(String originalText);

  @Query("SELECT lt.id as lemmaTypeId, lt.text as text, size(lt.textTokens) as count " +
          "FROM LemmaType lt ORDER BY size(lt.textTokens) desc")
  List<ShowMostCommonLemmasDTO> findMostCommonLemmasInNewsArticles(Pageable pageable);

}
