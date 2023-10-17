package com.example.stanfordnlpgerman.controllers;

import com.example.stanfordnlpgerman.models.dtos.NewsPaperEnum;
import com.example.stanfordnlpgerman.models.dtos.PhraseTypeEnum;
import com.example.stanfordnlpgerman.services.texttokenservice.TextTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("text-token")
public class TextTokenController {
  private final TextTokenService textTokenService;

  public TextTokenController(TextTokenService textTokenService) {
    this.textTokenService = textTokenService;
  }

  @GetMapping("invalid")
  public String getInvalidLemmas(Model model) {
    try {
      log.info("Logging");
      model.addAttribute("invalidLemmasDTOS", textTokenService.getInvalidLemmas());
      return "texttokens/invalid-tokens";
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
    }
    return "index";
  }

  @GetMapping("change/{textTokenId}/{textTokenText}")
  public String changeTextToken(@PathVariable long textTokenId, @PathVariable String textTokenText, Model model) {
    try {
      model.addAttribute("addLemmaTypeToTextTokenDTO",
          textTokenService.findLemmaTypeBelongingToTextTokenOrNoneIfNotPresent(textTokenId, textTokenText));
      model.addAttribute("phraseTypes", PhraseTypeEnum.values());
      return "texttokens/add-lemma-type";
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
    }
    return "index";
  }

  @GetMapping("delete/{textTokenId}")
  public String deleteInvalidTextToken(@PathVariable long textTokenId, Model model) {
    try {
      textTokenService.deleteById(textTokenId);
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
    }
    return "redirect:/text-token/invalid";
  }
}
