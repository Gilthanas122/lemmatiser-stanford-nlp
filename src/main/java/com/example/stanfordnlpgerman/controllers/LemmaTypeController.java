package com.example.stanfordnlpgerman.controllers;

import com.example.stanfordnlpgerman.models.dtos.lemmatype.UpdateLemmaTypeRequest;
import com.example.stanfordnlpgerman.services.lemmatypeservice.LemmaTypeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("lemma-type")
public class LemmaTypeController {
  private final LemmaTypeService lemmaTypeService;

  public LemmaTypeController(LemmaTypeService lemmaTypeService) {
    this.lemmaTypeService = lemmaTypeService;
  }

  @GetMapping("most-common/{pageNumber}/{keyword}")
  public String getMostCommonLemmas(@PathVariable short pageNumber, @PathVariable boolean keyword, Model model) {
    try {
      model.addAttribute("mostCommonLemmas", lemmaTypeService.findMostCommonLemmas(pageNumber, keyword));
      return "lemmatypes/most-common";
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
    }
    return "index";
  }

  @PostMapping("update/{textTokenId}")
  public String addTextTokenToLemmaType(@PathVariable long textTokenId, @ModelAttribute UpdateLemmaTypeRequest updateLemmaTypeRequest, Model model) {
    try {
      lemmaTypeService.addTextTokenToLemmaType(textTokenId, updateLemmaTypeRequest);
      return "redirect:/text-token/invalid";
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
    }
    return "index";
  }
}
