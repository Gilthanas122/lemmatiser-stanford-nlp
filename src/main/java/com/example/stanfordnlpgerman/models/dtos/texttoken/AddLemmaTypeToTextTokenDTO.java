package com.example.stanfordnlpgerman.models.dtos.texttoken;

import lombok.Builder;

import java.util.HashSet;
import java.util.Set;

@Builder
public class AddLemmaTypeToTextTokenDTO {
  private long textTokenId;
  private String textTokenText;
  private String sentenceText;
  private Set<String> lemmaTypeTexts = new HashSet<>();

  public AddLemmaTypeToTextTokenDTO() {
  }

  public AddLemmaTypeToTextTokenDTO(long textTokenId, String textTokenText, String sentenceText, Set<String> lemmaTypeTexts) {
    this.textTokenId = textTokenId;
    this.textTokenText = textTokenText;
    this.sentenceText = sentenceText;
    this.lemmaTypeTexts = lemmaTypeTexts;
  }

  public long getTextTokenId() {
    return textTokenId;
  }

  public void setTextTokenId(long textTokenId) {
    this.textTokenId = textTokenId;
  }

  public String getTextTokenText() {
    return textTokenText;
  }

  public void setTextTokenText(String textTokenText) {
    this.textTokenText = textTokenText;
  }

  public String getSentenceText() {
    return sentenceText;
  }

  public void setSentenceText(String sentenceText) {
    this.sentenceText = sentenceText;
  }

  public Set<String> getLemmaTypeTexts() {
    return lemmaTypeTexts;
  }

  public void setLemmaTypeTexts(Set<String> lemmaTypeTexts) {
    this.lemmaTypeTexts = lemmaTypeTexts;
  }
}
