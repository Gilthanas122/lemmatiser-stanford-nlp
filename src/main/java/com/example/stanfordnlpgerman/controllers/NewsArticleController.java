package com.example.stanfordnlpgerman.controllers;

import com.example.stanfordnlpgerman.exceptions.validations.MissingParamsException;
import com.example.stanfordnlpgerman.models.dtos.CreateNewsPaperArticleDTO;
import com.example.stanfordnlpgerman.services.newsarticleservice.NewsArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("newspaper")
public class NewsArticleController {
  private final NewsArticleService newsArticleService;

  public NewsArticleController(NewsArticleService newsArticleService) {
    this.newsArticleService = newsArticleService;
  }

  @GetMapping("create")
  public String generateFormForNewsArticle(Model model) {
    model.addAttribute("createNewsPapeArticleDTO", new CreateNewsPaperArticleDTO());
    return "newspaper/create";
  }

  @PostMapping("create")
  public String saveSubmittedNewsArticle(@ModelAttribute CreateNewsPaperArticleDTO createNewsPaperArticleDTO, Model model) {
    try {
      newsArticleService.saveArticle(createNewsPaperArticleDTO);
      return "index";
    } catch (MissingParamsException e) {
      model.addAttribute("error", e.getMessage());
      model.addAttribute("createNewsPapeArticleDTO", createNewsPaperArticleDTO);
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
      model.addAttribute("createNewsPapeArticleDTO", createNewsPaperArticleDTO);
    }
    return "newspaper/create";
  }
}
