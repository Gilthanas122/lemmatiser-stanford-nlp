package com.example.stanfordnlpgerman.services.sentenceservice;

import com.example.stanfordnlpgerman.exceptions.lemmatypes.LemmaTokenNotFoundByIdException;
import com.example.stanfordnlpgerman.models.dao.LemmaType;
import com.example.stanfordnlpgerman.models.dao.NewsArticle;
import com.example.stanfordnlpgerman.models.dao.Sentence;
import com.example.stanfordnlpgerman.models.dao.TextToken;
import com.example.stanfordnlpgerman.models.dtos.sentence.AdjacentSentencesToInvalidDTO;
import com.example.stanfordnlpgerman.models.dtos.sentence.InvalidSentencesDTO;
import com.example.stanfordnlpgerman.models.dtos.sentence.LemmaOccurenceInSentencesDTO;
import com.example.stanfordnlpgerman.models.dtos.sentence.SentenceTextAndNewsPaperIdDTO;
import com.example.stanfordnlpgerman.repositories.NewsArticleRepository;
import com.example.stanfordnlpgerman.repositories.SentenceRepository;
import com.example.stanfordnlpgerman.services.lemmatypeservice.LemmaTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SentenceServiceImpl implements SentenceService {
    private final SentenceRepository sentenceRepository;
    private final LemmaTypeService lemmaTypeService;
    private final NewsArticleRepository newsArticleRepository;

    public SentenceServiceImpl(SentenceRepository sentenceRepository, LemmaTypeService lemmaTypeService, NewsArticleRepository newsArticleRepository) {
        this.sentenceRepository = sentenceRepository;
        this.lemmaTypeService = lemmaTypeService;
        this.newsArticleRepository = newsArticleRepository;
    }

    @Override
    public List<SentenceTextAndNewsPaperIdDTO> getAllSentencesBelongingToLemmaType(long lemmaTypId) {
        List<SentenceTextAndNewsPaperIdDTO> sentenceTextAndNewsPaperIdDTOS = sentenceRepository.findAllByLemmaTypeId(lemmaTypId);
        log.info("{} Sentences found by lemmaType id {}", sentenceTextAndNewsPaperIdDTOS.size(), lemmaTypId);
        return sentenceTextAndNewsPaperIdDTOS;
    }

    @Override
    public List<LemmaOccurenceInSentencesDTO> showWordsInContext(long lemmaTypeId, int distance) throws LemmaTokenNotFoundByIdException {
        if (distance < 1) {
            return getContextFromFullSentence(lemmaTypeId);
        }

        LemmaType researchLemmaType = lemmaTypeService.findById(lemmaTypeId);
        List<TextToken> textTokens = sentenceRepository.sentencesContainingLemma(lemmaTypeId);
        List<Long> textTokenIds = getTextTokenIdsFromSentences(textTokens, lemmaTypeId);
        Map<String, Long> textTokensAndOccurences = filterTextTokensBasedOnDistance(textTokens, textTokenIds, distance, researchLemmaType.getText());

        return createFromMapListLemmaOccurenceInSentencesDTO(textTokensAndOccurences, researchLemmaType.getText());

    }

    @Override
    public List<InvalidSentencesDTO> getInvalidSentences() {
        List<InvalidSentencesDTO> invalidSentencesDTOS = sentenceRepository.getInvalidSentences();
        log.info("{} invalid sentences found", invalidSentencesDTOS.size());
        return invalidSentencesDTOS;
    }

    @Override
    public AdjacentSentencesToInvalidDTO getAdjacentSentences(long sentenceId, String text) {
        NewsArticle newsArticle = sentenceRepository.findNewsArticleBySentenceId(sentenceId);

        AdjacentSentencesToInvalidDTO adjacentSentencesToInvalidDTO = new AdjacentSentencesToInvalidDTO();
        StringBuilder sentenceText = new StringBuilder();
        newsArticle.getSentences().stream().sorted().map(Sentence::getText).forEach(sentenceText::append);

        adjacentSentencesToInvalidDTO.setSentencesText(sentenceText.toString());
        adjacentSentencesToInvalidDTO.setOriginalSentenceId(sentenceId);
        adjacentSentencesToInvalidDTO.setOriginalSentenceText(text);
        log.info("Adjacent sentence text: {}", adjacentSentencesToInvalidDTO.getSentencesText());
        return adjacentSentencesToInvalidDTO;
    }

    @Override
    public void fixInvalidSentences(int operation, long sentenceId) {
        switch (operation) {
            case 1 -> sentenceRepository.makeSentenceValidById(sentenceId);
            case 2 -> mergeWithOneSentence(sentenceId, true);
            case 3 -> mergeWithOneSentence(sentenceId, false);
            case 4 -> mergeWithAllAdjacentSentence(sentenceId);
            default -> {
                log.error("Invalid operation to merge sentences");
                throw new SentenceServiceException("Invalid operation to merge sentences");
            }
        }
    }

    private void mergeWithAllAdjacentSentence(long sentenceId) {
        NewsArticle newsArticle = sentenceRepository.findNewsArticleBySentenceId(sentenceId);
        Sentence sentencetoBeMergedWith = newsArticle.getSentences().stream()
                .filter(sentence -> sentence.getId() == sentenceId)
                .findFirst()
                .orElse(null);
        if (sentencetoBeMergedWith == null) {
            log.error("Could not find sentence by id {}", sentenceId);
            throw new SentenceServiceException(String.format("Could not find sentence by id %s", sentenceId));
        }

        Set<Sentence> adjacentSentences = new HashSet<>();

        Set<Sentence> sentences =
                newsArticle.getSentences().stream()
                        .peek(sentence -> {
                            if (sentence.getTextPosition()  == sentencetoBeMergedWith.getTextPosition() + 1 || sentence.getTextPosition() == sentencetoBeMergedWith.getTextPosition() - 1) {
                                sentence.setDeleted(true);
                                adjacentSentences.add(sentence);
                            } else if (sentence.getId() != sentencetoBeMergedWith.getId()){
                                sentence.setTextPosition((short) (sentence.getTextPosition() - 2));
                            }
                        })
                        .collect(Collectors.toSet());
        sentences.add(mergeSentences(adjacentSentences, sentencetoBeMergedWith));

        newsArticle.setSentences(new ArrayList<>(sentences));
        newsArticleRepository.save(newsArticle);
    }

    private Sentence mergeSentences(Set<Sentence> adjacentSentences, Sentence sentenceToBeMergedWith) {
        StringBuilder sentenceText = new StringBuilder(sentenceToBeMergedWith.getText());
        sentenceToBeMergedWith.setTextPosition((short) (sentenceToBeMergedWith.getTextPosition() - 1));
        adjacentSentences
                .forEach(sentence -> {
                    if (sentence.getTextPosition() < sentenceToBeMergedWith.getTextPosition()) {
                        sentenceText.insert(0, sentence.getText());
                        inheritDataFromDeletedSentence(sentenceToBeMergedWith, sentence);
                    } else {
                        sentenceText.append(sentence.getText());
                        inheritDataFromDeletedSentence(sentenceToBeMergedWith, sentence);
                    }
                });
        sentenceToBeMergedWith.setText(sentenceText.toString());
        return sentenceToBeMergedWith;
    }

    private void mergeWithOneSentence(long sentenceId, boolean isItPreviousToMergeWith) {
        NewsArticle newsArticle = sentenceRepository.findNewsArticleBySentenceId(sentenceId);
        short actualPosition = 0;
        boolean changeAfterThis = false;

        for (int i = 0; i < newsArticle.getSentences().size(); i++) {
            Sentence sentence = newsArticle.getSentences().get(i);
            if (sentence.getId() == sentenceId) {
                Sentence sentenceToDelete = new Sentence();
                if (isItPreviousToMergeWith) {
                    actualPosition = (short) (sentence.getTextPosition() - 1);
                    sentenceToDelete = newsArticle.getSentences().get(i - 1);
                    sentenceToDelete.setDeleted(true);
                } else {
                    sentenceToDelete = newsArticle.getSentences().get(i + 1);
                    sentenceToDelete.setDeleted(true);
                    actualPosition = sentence.getTextPosition();
                    i++;
                }
                sentence = inheritDataFromDeletedSentence(sentence, sentenceToDelete);
                sentence.setInvalid(false);
                changeAfterThis = true;
            }
            if (changeAfterThis) {
                sentence.setTextPosition(actualPosition);
                actualPosition++;
                sentenceRepository.save(sentence);
            }
        }
    }

    private Sentence inheritDataFromDeletedSentence(Sentence sentence, Sentence sentenceToDelete) {
        List<LemmaType> lemmaTypesFromToDelete = updateLemmaTypes(sentence, sentenceToDelete.getLemmaTypes(), sentenceToDelete);
        List<TextToken> textTokensFromToDelete = updateTextTokens(sentence, sentenceToDelete.getTextTokens());
        sentenceToDelete.setLemmaTypes(new HashSet<>());

        Set<LemmaType> sentenceLemmaTypes = sentence.getLemmaTypes();
        List<TextToken> sentenceTextTokens = sentence.getTextTokens();
        sentenceLemmaTypes.addAll(lemmaTypesFromToDelete);
        sentenceTextTokens.addAll(textTokensFromToDelete);

        sentence.setLemmaTypes(sentenceLemmaTypes);
        sentence.setTextTokens(sentenceTextTokens);
        sentence.setInvalid(false);
        return sentence;
    }

    private List<TextToken> updateTextTokens(Sentence sentence, List<TextToken> textTokens) {
        return textTokens
                .stream()
                .peek(s -> s.setSentence(sentence))
                .collect(Collectors.toList());
    }

    private List<LemmaType> updateLemmaTypes(Sentence sentence, Set<LemmaType> lemmaTypesFromToDelete, Sentence sentenceToDelete) {
        return lemmaTypesFromToDelete
                .stream()
                .peek(lemmaType -> {
                    lemmaType.addOneSentence(sentence);
                    lemmaType.removeOneSentence(sentenceToDelete);
                })
                .collect(Collectors.toList());
    }


    private List<LemmaOccurenceInSentencesDTO> createFromMapListLemmaOccurenceInSentencesDTO(Map<String, Long> textTokensAndOccurences, String lemmaTypeText) {
        List<LemmaOccurenceInSentencesDTO> lemmaOccurenceInSentencesDTOS = new LinkedList<>();
        for (Map.Entry<String, Long> entry : textTokensAndOccurences.entrySet()) {
            lemmaOccurenceInSentencesDTOS.add(createLemmaOccurenceInSentenceDTO(entry.getKey(), entry.getValue()));
        }

        lemmaOccurenceInSentencesDTOS.add(0, createLemmaOccurenceInSentenceDTO(lemmaTypeText, 0L));
        return lemmaOccurenceInSentencesDTOS;
    }

    private LemmaOccurenceInSentencesDTO createLemmaOccurenceInSentenceDTO(String key, Long value) {
        return new LemmaOccurenceInSentencesDTO() {
            @Override
            public String getOriginalLemmaText() {
                return key;
            }

            @Override
            public String getLemmaText() {
                return key;
            }

            @Override
            public Long getLemmaOccurence() {
                return value;
            }
        };
    }

    private Map<String, Long> filterTextTokensBasedOnDistance(List<TextToken> textTokens, List<Long> textTokenIds, int distance, String lemmaTypeText) {
        List<String> ll = new ArrayList<>();
        int textTokenCounter = 0;
        Map<String, Long> textTokensAndOccurences = new HashMap<>();
        for (int i = 0; i < textTokens.size(); i++) {
            if (textTokenCounter >= textTokenIds.size()) {
                break;
            } else if (textTokens.get(i).getId() == textTokenIds.get(textTokenCounter)) {
                for (int j = 0; j <= distance * 2; j++) {
                    if (i - distance + j < 0) {
                        continue;
                    } else if (textTokens.get(i).getSentence().getId() != textTokens.get(i - distance + j).getSentence().getId() || i - distance + j >= textTokens.size()) {
                        if (j > distance) {
                            break;
                        }
                        continue;
                    } else {
                        TextToken currentTextToken = textTokens.get(i - distance + j);
                        if (currentTextToken.getLemmaType() == null || currentTextToken.getLemmaType().getText().equals(lemmaTypeText)) {
                            continue;
                        } else if (textTokensAndOccurences.containsKey(currentTextToken.getLemmaType().getText())) {
                            textTokensAndOccurences.put(currentTextToken.getLemmaType().getText(), textTokensAndOccurences.get(currentTextToken.getLemmaType().getText()) + 1);
                        } else {
                            textTokensAndOccurences.put(currentTextToken.getLemmaType().getText(), 1L);
                        }
                    }
                }
                textTokenCounter++;
            }
        }
        return textTokensAndOccurences;
    }

    private List<Long> getTextTokenIdsFromSentences(List<TextToken> textTokens, long lemmaTypeId) {
        return textTokens
                .stream()
                .filter(tt -> tt.getLemmaType() != null && tt.getLemmaType().getId() == lemmaTypeId)
                .map(TextToken::getId)
                .collect(Collectors.toList());
    }

    private List<LemmaOccurenceInSentencesDTO> getContextFromFullSentence(long lemmaTypeId) {
        List<Long> sentenceIdsContainingLemma = sentenceRepository.sentenceIdsContainingLemma(lemmaTypeId);
        log.info("{} sentences found by lemmaType id: {}", sentenceIdsContainingLemma.size(), lemmaTypeId);
        return lemmaTypeService.findLemmasAndOccurencesInSentences(sentenceIdsContainingLemma, lemmaTypeId);
    }
}
