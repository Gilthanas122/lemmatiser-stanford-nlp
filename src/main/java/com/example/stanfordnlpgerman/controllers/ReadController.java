package com.example.stanfordnlpgerman.controllers;

import com.example.stanfordnlpgerman.services.newsarticleservice.ReadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReadController {
  private final ReadService readService;

  public ReadController(ReadService readService) {
    this.readService = readService;
  }

  @GetMapping("start-reading")
  public String startReading(Model model){
    try {
      readService.startReading();
    }catch (Exception e){
      model.addAttribute("error", e.getMessage());
    }
    return "index";
  }

}
