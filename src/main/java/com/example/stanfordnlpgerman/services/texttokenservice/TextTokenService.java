package com.example.stanfordnlpgerman.services.texttokenservice;

import com.example.stanfordnlpgerman.models.dao.TextToken;
import com.example.stanfordnlpgerman.models.dtos.lemmatype.InvalidLemmasDTO;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface TextTokenService {
  void saveTextTokenWithoutLemmaType(TextToken textToken);

  Set<InvalidLemmasDTO> getInvalidLemmas();
}
