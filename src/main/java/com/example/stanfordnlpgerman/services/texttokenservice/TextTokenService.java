package com.example.stanfordnlpgerman.services.texttokenservice;

import com.example.stanfordnlpgerman.models.dao.TextToken;
import org.springframework.stereotype.Service;

@Service
public interface TextTokenService {
  void saveTextTokenWithoutLemmaType(TextToken textToken);
}
