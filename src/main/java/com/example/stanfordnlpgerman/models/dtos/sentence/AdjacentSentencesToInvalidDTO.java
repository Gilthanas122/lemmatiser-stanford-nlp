package com.example.stanfordnlpgerman.models.dtos.sentence;

public class AdjacentSentencesToInvalidDTO {
  private String sentencesText;
  private long originalSentenceId;
  private String originalSentenceText;

  public AdjacentSentencesToInvalidDTO() {
  }

  public AdjacentSentencesToInvalidDTO(String sentencesText, long originalSentenceId, String originalSentenceText) {
    this.sentencesText = sentencesText;
    this.originalSentenceId = originalSentenceId;
    this.originalSentenceText = originalSentenceText;
  }

  public String getSentencesText() {
    return sentencesText;
  }

  public void setSentencesText(String sentencesText) {
    this.sentencesText = sentencesText;
  }

  public long getOriginalSentenceId() {
    return originalSentenceId;
  }

  public void setOriginalSentenceId(long originalSentenceId) {
    this.originalSentenceId = originalSentenceId;
  }

  public String getOriginalSentenceText() {
    return originalSentenceText;
  }

  public void setOriginalSentenceText(String originalSentenceText) {
    this.originalSentenceText = originalSentenceText;
  }
}
