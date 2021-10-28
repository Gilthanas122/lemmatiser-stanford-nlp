package com.example.stanfordnlpgerman.controllers;

import com.example.stanfordnlpgerman.services.texttokenservice.TextTokenService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("text-token")
public class TextTokenController {
  private final TextTokenService textTokenService;

  public TextTokenController(TextTokenService textTokenService) {
    this.textTokenService = textTokenService;
  }

  @GetMapping("invalid-text-tokens")
  public String getInvalidLemmas(Model model) {
    try {
      model.addAttribute("invalidLemmasDTOS", textTokenService.getInvalidLemmas());
      return "texttokens/invalid-tokens";
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
    }
    return "index";
  }

  @GetMapping("change/{textTokenId}/{textTokenText}")
  public String changeTextToken(@PathVariable long textTokenId, @PathVariable String textTokenText, Model model){
    try {
      model.addAttribute("addLemmaTypeToTextTokenDTO",textTokenService.findLemmaTypeBelongingToTextTokenOrNoneIfNotPresent(textTokenId, textTokenText));
      return "texttokens/add-lemma-type";
    }catch (Exception e){
      model.addAttribute("error", e.getMessage());
    }
    return "index";
  }
}
