package com.example.stanfordnlpgerman.controllers;

import com.example.stanfordnlpgerman.services.sentenceservice.SentenceService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("sentence")
public class SentenceController {
  private final SentenceService sentenceService;

  public SentenceController(SentenceService sentenceService) {
    this.sentenceService = sentenceService;
  }
}
