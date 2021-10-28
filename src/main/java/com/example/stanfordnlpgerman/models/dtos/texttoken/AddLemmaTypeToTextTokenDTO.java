package com.example.stanfordnlpgerman.models.dtos.texttoken;

import lombok.Builder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Builder
public class AddLemmaTypeToTextTokenDTO {
  private long textTokenId;
  private String textTokenText;
  private String sentenceText;
  private Map<Long, String> lemmaTypeTextsAndIds = new HashMap();

  public AddLemmaTypeToTextTokenDTO() {
  }

  public AddLemmaTypeToTextTokenDTO(long textTokenId, String textTokenText, String sentenceText, Map<Long, String> lemmaTypeTextsAndIds) {
    this.textTokenId = textTokenId;
    this.textTokenText = textTokenText;
    this.sentenceText = sentenceText;
    this.lemmaTypeTextsAndIds = lemmaTypeTextsAndIds;
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

  public Map<Long, String> getLemmaTypeTextsAndIds() {
    return lemmaTypeTextsAndIds;
  }

  public void setLemmaTypeTextsAndIds(Map<Long, String> lemmaTypeTextsAndIds) {
    this.lemmaTypeTextsAndIds = lemmaTypeTextsAndIds;
  }
}
