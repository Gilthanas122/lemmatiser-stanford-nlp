package com.example.stanfordnlpgerman.controllers;

import com.example.stanfordnlpgerman.exceptions.validations.MissingParamsException;
import com.example.stanfordnlpgerman.models.dtos.CreateNewsPaperArticleDTO;
import com.example.stanfordnlpgerman.services.lemmaservice.LemmaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("newspaper")
public class NewsArticleController {
  private final LemmaService lemmaService;

  public NewsArticleController(LemmaService lemmaService) {
    this.lemmaService = lemmaService;
  }

  @GetMapping("create")
  public String generateFormForNewsArticle(Model model){
    model.addAttribute("createNewsPapeArticleDTO", new CreateNewsPaperArticleDTO());
    return "newspaper/create";
  }

  @PostMapping("create")
  public String saveSubmittedNewsArticle(@ModelAttribute CreateNewsPaperArticleDTO createNewsPaperArticleDTO, Model model){
    try {
      lemmaService.saveArticle(createNewsPaperArticleDTO);
      return "index";
    }catch (MissingParamsException e){
      model.addAttribute("error", e.getMessage());
      model.addAttribute("createNewsPapeArticleDTO", createNewsPaperArticleDTO);
    }
    catch (Exception e){
      model.addAttribute("error", e.getMessage());
      model.addAttribute("createNewsPapeArticleDTO", createNewsPaperArticleDTO);
    }
    return "newspaper/create";
  }
}
