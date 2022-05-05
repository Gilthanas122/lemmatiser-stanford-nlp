package com.example.stanfordnlpgerman.testobjectcreation;

import com.example.stanfordnlpgerman.models.dao.LemmaType;
import com.example.stanfordnlpgerman.models.dao.Sentence;
import com.example.stanfordnlpgerman.models.dao.TextToken;

import java.util.ArrayList;
import java.util.List;

public class TextTokenCreator {

  public static List<TextToken> createTextTokens(int amount){
    List<TextToken> textTokens = new ArrayList<>();
    for (int index = 1; index <= amount ; index++) {
      textTokens.add(createTextToken(index));
    }
    return textTokens;
  }

  public static TextToken createTextToken(int index) {
    return TextToken
            .builder()
            .id(index)
            .text("text token text " + index)
            .phraseType("VERB")
            .sentencePosition((short) index)
            .deleted(false)
            .invalid(true)
            .lemmaType(new LemmaType())
            .sentence(new Sentence())
            .build();
  }
}
