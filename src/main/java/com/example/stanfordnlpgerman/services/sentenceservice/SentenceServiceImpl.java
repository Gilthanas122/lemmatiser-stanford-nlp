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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
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
        List<TextToken> textTokens = sentenceRepository.findAllTextTokensInSentencesContainingLemma(lemmaTypeId);
        List<Short> textTokenSentencePositions = getTextTokenIdsFromSentences(textTokens, lemmaTypeId);
        Map<String, Integer> textTokensAndOccurences = filterTextTokensBasedOnDistance(textTokens, textTokenSentencePositions, distance, researchLemmaType.getText());

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
        Sentence sentenceToBeMergedWith = sentenceRepository.findById(sentenceId).orElse(null);
        if (sentenceToBeMergedWith == null) {
            log.error("Could not find sentence by id {}", sentenceId);
            throw new SentenceServiceException(String.format("Could not find sentence by id %s", sentenceId));
        }

        Function<Sentence, Boolean> mergeWithPreviousSentence = sentence -> sentence.getTextPosition() == sentenceToBeMergedWith.getTextPosition() - 1;
        Function<Sentence, Boolean> mergeWithNextSentence = sentence -> sentence.getTextPosition() == sentenceToBeMergedWith.getTextPosition() + 1;
        Function<Sentence, Boolean> mergeBothAdjacentSentences = sentence -> sentence.getTextPosition() == sentenceToBeMergedWith.getTextPosition() + 1
                || sentence.getTextPosition() == sentenceToBeMergedWith.getTextPosition() - 1;
        switch (operation) {
            case 1 -> sentenceRepository.makeSentenceValidById(sentenceId); // do not merge
            case 2 -> mergeSentences(sentenceId, (short) 1, (short) 1, mergeWithPreviousSentence, sentenceToBeMergedWith);
            case 3 -> mergeSentences(sentenceId, (short) 1, (short) 0, mergeWithNextSentence, sentenceToBeMergedWith);
            case 4 -> mergeSentences(sentenceId, (short) 2, (short) 1, mergeBothAdjacentSentences, sentenceToBeMergedWith);
            default -> {
                log.error("Invalid operation to merge sentences");
                throw new SentenceServiceException("Invalid operation to merge sentences");
            }
        }
    }

    private void mergeSentences(long sentenceId, short textPositionDecrease, short textPositionDecreaseForMergeSentence,
                                Function<Sentence, Boolean> sentenceComparisonByTextPosition, Sentence sentenceToBeMergedWith) {
        NewsArticle newsArticle = sentenceRepository.findNewsArticleBySentenceId(sentenceId);

        Set<Sentence> adjacentSentences = new HashSet<>();

        Set<Sentence> sentences =
                newsArticle.getSentences().stream()
                        .peek(sentence -> {
                            if (sentenceComparisonByTextPosition.apply(sentence)) {
                                sentence.setDeleted(true);
                                adjacentSentences.add(sentence);
                            } else if (sentence.getId() != sentenceToBeMergedWith.getId()
                                    && sentence.getTextPosition() > sentenceToBeMergedWith.getTextPosition()) {
                                sentence.setTextPosition((short) (sentence.getTextPosition() - textPositionDecrease));
                            }
                        })
                        .collect(Collectors.toSet());
        sentences.add(mergeSentenceData(adjacentSentences, sentenceToBeMergedWith, textPositionDecreaseForMergeSentence));

        newsArticle.setSentences(new ArrayList<>(sentences));
        newsArticleRepository.save(newsArticle);
    }

    private Sentence mergeSentenceData(Set<Sentence> adjacentSentences, Sentence sentenceToBeMergedWith, short textPositionDecreaseForMergeSentence) {
        StringBuilder sentenceText = new StringBuilder(sentenceToBeMergedWith.getText());
        adjacentSentences
                .forEach(sentence -> {
                    if (sentence.getTextPosition() < sentenceToBeMergedWith.getTextPosition()) {
                        sentenceText.insert(0, sentence.getText());
                        inheritDataFromDeletedSentence(sentenceToBeMergedWith, sentence, true);
                    } else {
                        sentenceText.append(sentence.getText());
                        inheritDataFromDeletedSentence(sentenceToBeMergedWith, sentence, false);
                    }
                });
        sentenceToBeMergedWith.setTextPosition((short) (sentenceToBeMergedWith.getTextPosition() - textPositionDecreaseForMergeSentence));
        sentenceToBeMergedWith.setText(sentenceText.toString());
        return sentenceToBeMergedWith;
    }

    private void inheritDataFromDeletedSentence(Sentence sentence, Sentence sentenceToDelete, boolean isItPreviousSentence) {
        List<LemmaType> lemmaTypesFromToDelete = updateLemmaTypes(sentence, sentenceToDelete.getLemmaTypes(), sentenceToDelete);
        List<TextToken> textTokensWithNewsSentencePosition = updateTextTokens(sentence, sentenceToDelete.getTextTokens(), isItPreviousSentence);
        sentenceToDelete.setLemmaTypes(new HashSet<>());

        Set<LemmaType> sentenceLemmaTypes = sentence.getLemmaTypes();
        sentenceLemmaTypes.addAll(lemmaTypesFromToDelete);

        sentence.setLemmaTypes(sentenceLemmaTypes);
        sentence.setTextTokens(textTokensWithNewsSentencePosition);
        sentence.setInvalid(false);
    }

    private List<TextToken> updateTextTokens(Sentence sentence, List<TextToken> textTokens, boolean isItPreviousSentence) {
        List<TextToken> textTokensWithSentenceSet = textTokens
                .stream()
                .peek(textToken -> textToken.setSentence(sentence))
                .collect(Collectors.toList());

        return setNewPositionForTextTokens(sentence.getTextTokens(), textTokensWithSentenceSet, isItPreviousSentence);
    }

    private List<TextToken> setNewPositionForTextTokens(List<TextToken> sentenceTextTokens, List<TextToken> toBeDeletedSentenceTextToken, boolean isItPreviousSentence) {
        short startPositionForSentencePosition = (short) (isItPreviousSentence ? toBeDeletedSentenceTextToken.size() : sentenceTextTokens.size());
        AtomicInteger sentencePositionToStartFrom = new AtomicInteger(startPositionForSentencePosition);
        List<TextToken> textTokensWithNewSentencePositionSet = new ArrayList<>();
        if (isItPreviousSentence) {
            textTokensWithNewSentencePositionSet.addAll(toBeDeletedSentenceTextToken);
            sentenceTextTokens.forEach(textToken -> {
                textToken.setSentencePosition((short) sentencePositionToStartFrom.get());
                textTokensWithNewSentencePositionSet.add(textToken);
                sentencePositionToStartFrom.incrementAndGet();
            });
        } else {
            textTokensWithNewSentencePositionSet.addAll(sentenceTextTokens);
            toBeDeletedSentenceTextToken.forEach(textToken -> {
                textToken.setSentencePosition((short) sentencePositionToStartFrom.get());
                textTokensWithNewSentencePositionSet.add(textToken);
                sentencePositionToStartFrom.incrementAndGet();
            });
        }
        return textTokensWithNewSentencePositionSet;
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


    private List<LemmaOccurenceInSentencesDTO> createFromMapListLemmaOccurenceInSentencesDTO(Map<String, Integer> textTokensAndOccurences, String lemmaTypeText) {
        List<LemmaOccurenceInSentencesDTO> lemmaOccurenceInSentencesDTOS = new LinkedList<>();
        textTokensAndOccurences.forEach((lemmaText, occurence) -> {
            lemmaOccurenceInSentencesDTOS.add(createLemmaOccurenceInSentenceDTO(lemmaText, occurence));
        });

        lemmaOccurenceInSentencesDTOS.add(0, createLemmaOccurenceInSentenceDTO(lemmaTypeText, 0));
        return lemmaOccurenceInSentencesDTOS;
    }

    private LemmaOccurenceInSentencesDTO createLemmaOccurenceInSentenceDTO(String key, Integer value) {
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
            public Integer getLemmaOccurence() {
                return value;
            }
        };
    }

    private Map<String, Integer> filterTextTokensBasedOnDistance(List<TextToken> textTokens, List<Short> textTokenSentencePositions, int distance, String lemmaTypeText) {
        AtomicInteger tokenCounter = new AtomicInteger(0);
        Map<String, Integer> textTokensAndOccurences = new HashMap<>();

        textTokens.forEach(textToken -> {
            if (checkIfValidDistance(textToken.getSentencePosition(), textTokenSentencePositions, distance)) {
                if (textTokensAndOccurences.containsKey(textToken.getText())) {
                    textTokensAndOccurences.put(textToken.getText(), textTokensAndOccurences.get(textToken.getText()) + 1);
                } else {
                    textTokensAndOccurences.put(textToken.getText(), 1);
                }
            }
        });

        return textTokensAndOccurences;
    }

    private boolean checkIfValidDistance(short textTokenPosition, List<Short> textTokenSentencePositions, int distance) {
        boolean[] valid = {false};
        textTokenSentencePositions.forEach(position -> {
            if (textTokenPosition + distance >= position || textTokenPosition - distance <= position) {
                valid[0] = true;
            }
        });
        return valid[0];
    }

    private List<Short> getTextTokenIdsFromSentences(List<TextToken> textTokens, long lemmaTypeId) {
        return textTokens
                .stream()
                .filter(tt -> tt.getLemmaType() != null && tt.getLemmaType().getId() == lemmaTypeId)
                .map(TextToken::getSentencePosition)
                .toList();
    }

    private List<LemmaOccurenceInSentencesDTO> getContextFromFullSentence(long lemmaTypeId) {
        List<Long> sentenceIdsContainingLemma = sentenceRepository.sentenceIdsContainingLemma(lemmaTypeId);
        log.info("{} sentences found by lemmaType id: {}", sentenceIdsContainingLemma.size(), lemmaTypeId);
        return lemmaTypeService.findLemmasAndOccurencesInSentences(sentenceIdsContainingLemma, lemmaTypeId);
    }
}
