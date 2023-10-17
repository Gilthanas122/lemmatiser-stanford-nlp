package com.example.stanfordnlpgerman.component;

import java.util.List;

public class SentenceCreationResources {
  private static final List<String> verbPhrasals = List.of("durch", "hinter", "um", "über", "unter", "voll", "wieder", "ab", "an", "auf", "aus", "ein", "entlang", "gegenüber",
      "heim", "her", "her", "herab", "herauf", "heraus", "herein", "herüber", "hin", "hinab", "hinauf", "hinaus", "hinein", "hinüber", "los", "mit", "nach", "umher", "rückwärts",
      "vor", "voran", "voraus", "vorbei", "vorher", "vorwärts", "vorüber", "vorweg", "weg", "zu", "zusammen", "zurück");

  private static final List<String> signsForConjunctiveVerbs = List.of(",", ".", "?", "!", ";", "und", "oder");

  public static boolean isPhrasal(String token){
    return verbPhrasals.contains(token);
  }

  public static boolean isSentenceSign(String sign){
    return signsForConjunctiveVerbs.contains(sign);
  }
}
