package com.example.stanfordnlpgerman.controllers;

import com.example.stanfordnlpgerman.services.texttokenservice.TextTokenService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
}
