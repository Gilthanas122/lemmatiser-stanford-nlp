package com.example.stanfordnlpgerman.models.dtos.sentence;

import java.util.List;

public interface LemmaOccurenceInSentencesDTO {
  List<String> getLemmaTexts();

  List<Long> getLemmaOccurences();
}
