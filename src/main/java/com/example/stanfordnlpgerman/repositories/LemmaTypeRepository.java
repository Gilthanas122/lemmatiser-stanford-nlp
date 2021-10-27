package com.example.stanfordnlpgerman.repositories;

import com.example.stanfordnlpgerman.models.dao.LemmaType;
import com.example.stanfordnlpgerman.models.dtos.lemmatype.ShowMostCommonLemmasDTO;
import com.example.stanfordnlpgerman.models.dtos.sentence.LemmaOccurenceInSentencesDTO;
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

  @Query("SELECT lt.id AS lemmaTypeId, lt.text AS text, size(lt.textTokens) AS count, na.id AS newsArticleId " +
          "FROM LemmaType lt JOIN lt.newsArticles na WHERE size(lt.textTokens) > 0 ORDER BY size(lt.textTokens) desc")
  List<ShowMostCommonLemmasDTO> findMostCommonLemmasInNewsArticles(Pageable pageable);

  @Query("SELECT lt.text AS lemmaText, COUNT(lt.id) AS lemmaOccurence, (SELECT ltoriginal.text FROM LemmaType ltoriginal WHERE ltoriginal.id = ?2) AS originalLemmaText FROM LemmaType lt JOIN lt.sentences s WHERE s.id IN ?1 AND lt.id <> ?2 GROUP BY lt.id")
  List<LemmaOccurenceInSentencesDTO> findLemmaTypeOccurencesInSentences(List<Long> sentenceIdsContainingLemma, long lemmaTypeId);
}
