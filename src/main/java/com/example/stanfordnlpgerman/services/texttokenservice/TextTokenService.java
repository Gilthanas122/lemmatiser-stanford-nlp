package com.example.stanfordnlpgerman.services.texttokenservice;

import com.example.stanfordnlpgerman.models.dao.TextToken;
import com.example.stanfordnlpgerman.models.dtos.lemmatype.InvalidLemmasDTO;
import com.example.stanfordnlpgerman.models.dtos.texttoken.AddLemmaTypeToTextTokenDTO;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface TextTokenService {

  Set<InvalidLemmasDTO> getInvalidLemmas();

  AddLemmaTypeToTextTokenDTO findLemmaTypeBelongingToTextTokenOrNoneIfNotPresent(long textTokenId, String textTokenText);

  void deleteById(long textTokenId);
}
