package com.example.stanfordnlpgerman.repositories;

import com.example.stanfordnlpgerman.models.dao.LemmaType;
import com.example.stanfordnlpgerman.models.dtos.lemmatype.ShowMostCommonLemmasDTO;
import com.example.stanfordnlpgerman.models.dtos.sentence.LemmaOccurenceInSentencesDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface LemmaTypeRepository extends JpaRepository<LemmaType, Long>, PagingAndSortingRepository<LemmaType, Long> {

  @Query("SELECT lt FROM LemmaType lt JOIN lt.lemmaTokens lto " +
          "WHERE lt.text = ?1 OR lto.text = ?1")
  Set<LemmaType> findAllByLemmaText(String originalText);

  @Query("SELECT lt.id AS lemmaTypeId, lt.text AS text, size(lt.textTokens) AS count, na.id AS newsArticleId " +
          "FROM LemmaType lt JOIN lt.newsArticles na where size(lt.textTokens) > 0 ORDER BY size(lt.textTokens) desc")
  List<ShowMostCommonLemmasDTO> findMostCommonLemmasInNewsArticles(PageRequest pageable);

  @Query("SELECT lt.text AS lemmaText, COUNT(lt.id) AS lemmaOccurence, " +
          "(SELECT ltoriginal.text FROM LemmaType ltoriginal WHERE ltoriginal.id = ?2) AS originalLemmaText " +
          "FROM LemmaType lt JOIN lt.sentences s WHERE s.id IN ?1 AND lt.id <> ?2 GROUP BY lt.id")
  List<LemmaOccurenceInSentencesDTO> findLemmaTypeOccurencesInSentences(List<Long> sentenceIdsContainingLemma, long lemmaTypeId);

  @Modifying
  @Query("UPDATE TextToken tt SET tt.invalid = false, tt.lemmaType.id = ?2 WHERE tt.text = ?1")
  void updateIfLemmaTypeHasMatchingTextTokens(String lemmaTypeText, long lemmaTypeId);

  @Query("SELECT DISTINCT lt.id AS lemmaTypeId, lt.text AS text, size(lt.textTokens) AS count, na.id AS newsArticleId " +
          "FROM LemmaType lt JOIN lt.newsArticles na JOIN lt.textTokens tt WHERE size(lt.textTokens) > 0 AND tt.text IN ?1 OR lt.text IN ?1 ORDER BY size(lt.textTokens) desc")
  List<ShowMostCommonLemmasDTO> findMostCommonLemmasInNewsArticlesByKeyWords(Set<String> keyWords, PageRequest pageRequest);

  LemmaType findByText(String lemmaTypeText);
}
