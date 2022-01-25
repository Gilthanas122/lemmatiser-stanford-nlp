package com.example.stanfordnlpgerman.models;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KeyWordsSingleton {
  private static final Set<String> keyWords =
          new HashSet<>(Arrays.asList("Ungarndeutsch", "Wir", "Familie"));

  public static Set<String> getKeyWords() {
    return keyWords;
  }
}
