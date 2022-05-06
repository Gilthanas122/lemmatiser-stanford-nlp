package com.example.stanfordnlpgerman.testobjectcreation;

import com.example.stanfordnlpgerman.models.dao.Sentence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SentenceCreator {
  public static final String TEXT = "Sentence text";


  public static List<Sentence> createSentences(int amount){
    List<Sentence> sentences = new ArrayList<>();
    for (int index = 1; index <= amount; index++) {
      sentences.add(createSentence(index));
    }
    return sentences;
  }

  private static Sentence createSentence(int index) {
    return Sentence.builder()
            .id(index)
            .text("Sentence text" + index)
            .textPosition((short) index)
            .lemmaTypes(new HashSet<>())
            .textTokens(new ArrayList<>())
            .build();
  }
}
