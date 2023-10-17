package com.example.stanfordnlpgerman.models;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KeyWordsSingleton {
  @Getter
  private final static Set<String> keyWords =
          new HashSet<>(Arrays.asList("Ungarndeutsch", "Wir", "Familie"));

  public static boolean  isKeyWord(String word) {
    return keyWords.contains(word);
  }

}
