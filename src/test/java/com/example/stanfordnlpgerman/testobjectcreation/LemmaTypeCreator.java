package com.example.stanfordnlpgerman.testobjectcreation;

import com.example.stanfordnlpgerman.models.dao.LemmaType;
import com.example.stanfordnlpgerman.models.dao.Sentence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LemmaTypeCreator {
  private static final String LEMMA_TYPE_TEXt = "Lemma type text";

  public static List<LemmaType> createLemmaTypes(int amount){
    List<LemmaType> lemmaTypes = new ArrayList<>();
    for (int index = 1; index <= amount ; index++) {
      lemmaTypes.add(createLemmaType(index));
    }
    return lemmaTypes;
  }

  public static LemmaType createLemmaType(int index) {
    return LemmaType.builder()
            .text("Lemma type text" + index)
            .id(index)
            .sentences(new ArrayList<>())
            .lemmaTokens(new ArrayList<>())
            .newsArticles(new ArrayList<>())
            .textTokens(new ArrayList<>())
            .build();
  }
}
