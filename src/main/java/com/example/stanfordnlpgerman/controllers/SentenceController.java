package com.example.stanfordnlpgerman.controllers;

import com.example.stanfordnlpgerman.models.dtos.sentence.LemmaOccurenceInSentencesDTO;
import com.example.stanfordnlpgerman.services.sentenceservice.SentenceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("sentence")
public class SentenceController {
  private final SentenceService sentenceService;

  public SentenceController(SentenceService sentenceService) {
    this.sentenceService = sentenceService;
  }

  @GetMapping("get/{lemmaTypId}")
  public String getAllSentencesFromLemmaType(@PathVariable long lemmaTypId, Model model) {
    try {
      model.addAttribute("sentenceTextAndNewsPaperIdDTOs", sentenceService.getAllSentencesBelongingToLemmaType(lemmaTypId));
      return "sentence/list";
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
    }
    return "index";
  }

  @GetMapping("context/{lemmaTypeId}")
  @ResponseBody
  public List<LemmaOccurenceInSentencesDTO> showWordsInContext(@PathVariable long lemmaTypeId, Model model) {
    return sentenceService.showWordsInContext(lemmaTypeId);
  }
}
