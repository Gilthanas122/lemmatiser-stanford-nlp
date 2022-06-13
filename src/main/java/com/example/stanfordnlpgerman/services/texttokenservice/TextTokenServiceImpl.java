package com.example.stanfordnlpgerman.services.texttokenservice;

import com.example.stanfordnlpgerman.models.dao.LemmaType;
import com.example.stanfordnlpgerman.models.dao.TextToken;
import com.example.stanfordnlpgerman.models.dtos.lemmatype.InvalidLemmasDTO;
import com.example.stanfordnlpgerman.models.dtos.texttoken.AddLemmaTypeToTextTokenDTO;
import com.example.stanfordnlpgerman.repositories.TextTokenRepository;
import com.example.stanfordnlpgerman.services.lemmatypeservice.LemmaTypeService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TextTokenServiceImpl implements TextTokenService {
  private final TextTokenRepository textTokenRepository;
  private final LemmaTypeService lemmaTypeService;

  public TextTokenServiceImpl(TextTokenRepository textTokenRepository, LemmaTypeService lemmaTypeService) {
    this.textTokenRepository = textTokenRepository;
    this.lemmaTypeService = lemmaTypeService;
  }


  @Override
  public Set<InvalidLemmasDTO> getInvalidLemmas() {
    Set<TextToken> textTokens = textTokenRepository.getTextTokensInvalid();
    Set<InvalidLemmasDTO> invalidLemmasDTOS = new HashSet<>();
    for (TextToken tt : textTokens) {
      invalidLemmasDTOS.add(InvalidLemmasDTO
          .builder()
          .textTokenId(tt.getId())
          .textTokenText(tt.getText())
          .build());
    }
    return invalidLemmasDTOS;
  }

  @Override
  public AddLemmaTypeToTextTokenDTO findLemmaTypeBelongingToTextTokenOrNoneIfNotPresent(long textTokenId, String textTokenText) {
    TextToken textToken = textTokenRepository.findById(textTokenId);
    Set<LemmaType> lemmaTypes = lemmaTypeService.findAllByText(textTokenText);
    Map<Long, String> lemmaTypeTextByLemmaTypeId =
        lemmaTypes.stream()
            .collect(Collectors.toMap(LemmaType::getId, LemmaType::getText));

    return AddLemmaTypeToTextTokenDTO
        .builder()
        .lemmaTypeTextsAndIds(lemmaTypeTextByLemmaTypeId)
        .sentenceText(textToken.getSentence().getText())
        .textTokenId(textTokenId)
        .textTokenText(textTokenText)
        .phraseType(textToken.getPhraseType())
        .build();
  }

  @Override
  public void deleteById(long textTokenId) {
    textTokenRepository.setToBeDeleted(textTokenId);
  }

}
