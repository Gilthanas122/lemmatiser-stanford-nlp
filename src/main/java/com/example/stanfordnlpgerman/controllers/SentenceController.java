package com.example.stanfordnlpgerman.controllers;

import com.example.stanfordnlpgerman.services.sentenceservice.SentenceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
  public String searchLemmaTypeIdAndContext(@PathVariable long lemmaTypeId, Model model) {
    model.addAttribute("lemmaTypeId", lemmaTypeId);
    return "lemmatypes/context";
  }

  @PostMapping("context/{lemmaTypeId}")
  public String showWordsInContext(@PathVariable long lemmaTypeId, Model model, @RequestParam int distance) {
    try {
      model.addAttribute("lemmaOccurenceInSentencesDTOs", sentenceService.showWordsInContext(lemmaTypeId, distance));
      return "sentence/show-lemmas-context";
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
    }
    return "index";
  }

  @GetMapping("invalid")
  public String getInvalidSentences(Model model) {
    try {
      model.addAttribute("invalidSentences", sentenceService.getInvalidSentences());
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
    }
    return "sentence/invalid";
  }

  @PostMapping("fix-invalid/{id}")
  public String fixInvalidSentences(@RequestParam String text, Model model, @PathVariable long id) {
    try {
      model.addAttribute("adjacentSentencesToInvalidDTO", sentenceService.getAdjacentSentences(id, text));
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
    }
    return "sentence/fix-invalid";
  }

  @GetMapping("fix-invalid/{operation}/{id}")
  public String fixSentencesByMergingThem(@PathVariable int operation, @PathVariable long id, Model model) {
    try {
      sentenceService.fixInvalidSentences(operation, id);
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
    }
    return "redirect:/sentence/invalid";
  }

}
