package com.example.stanfordnlpgerman.models.dtos.lemmatype;

import lombok.Builder;

@Builder
public class InvalidLemmasDTO {
  private long textTokenId;
  private String textTokenText;

  public InvalidLemmasDTO() {
  }

  public InvalidLemmasDTO(long textTokenId, String textTokenText) {
    this.textTokenId = textTokenId;
    this.textTokenText = textTokenText;
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
}
