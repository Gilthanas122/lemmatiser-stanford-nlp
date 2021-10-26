package com.example.stanfordnlpgerman.controllers;

import com.example.stanfordnlpgerman.services.lemmatypeservice.LemmaTypeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("lemma-type")
public class LemmaTypeController {
  private final LemmaTypeService lemmaTypeService;

  public LemmaTypeController(LemmaTypeService lemmaTypeService) {
    this.lemmaTypeService = lemmaTypeService;
  }

  @GetMapping("most-common/{pageNumber}")
  public String getMostCommonLemmas(@PathVariable short pageNumber, Model model){
    try {
      model.addAttribute("mostCommonLemmas", lemmaTypeService.findMostCommonLemmas(pageNumber));
      return "lemmatypes/most-common";
    }catch (Exception e){
      model.addAttribute("error", e.getMessage());
    }
    return "index";
  }

}
