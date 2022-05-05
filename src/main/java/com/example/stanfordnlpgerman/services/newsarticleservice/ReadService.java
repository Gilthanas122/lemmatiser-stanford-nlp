package com.example.stanfordnlpgerman.services.newsarticleservice;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface ReadService {
  void startReading() throws Exception;
}
