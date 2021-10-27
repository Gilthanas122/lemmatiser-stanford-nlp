package com.example.stanfordnlpgerman.controllers;

import com.example.stanfordnlpgerman.exceptions.validations.MissingParamsException;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.CreateNewsPaperArticleDTO;
import com.example.stanfordnlpgerman.services.newsarticleservice.NewsArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("news-article")
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

  @GetMapping("/get/{newsArticleId}")
  public String getNewsArticleBelongingToSentence(@PathVariable long newsArticleId, Model model){
    try {
      model.addAttribute("newsArticleDataDTO", newsArticleService.findByNewsArticleId(newsArticleId));
      return "newspaper/list";
    }catch (Exception e){
      model.addAttribute("error", e.getMessage());
    }
    return "index";
  }
}
