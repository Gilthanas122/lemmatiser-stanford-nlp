package com.example.stanfordnlpgerman.services.newsarticleservice;

import com.example.stanfordnlpgerman.models.KeyWordsSingleton;
import com.example.stanfordnlpgerman.models.dao.*;
import com.example.stanfordnlpgerman.models.dtos.newsarticle.CreateNewsPaperArticleDTO;
import com.example.stanfordnlpgerman.repositories.NewsArticleRepository;
import com.example.stanfordnlpgerman.services.lemmatypeservice.LemmaTypeService;
import com.example.stanfordnlpgerman.services.validations.ErrorServiceImpl;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class NewsArticleAsyncServiceImpl implements NewsArticleAsyncService {
    private final List<TextToken> filteredTextTokens = new ArrayList<>();
    private final NewsArticleRepository newsArticleRepository;
    private final LemmaTypeService lemmaTypeService;
    private final StanfordCoreNLP pipeline;

    public NewsArticleAsyncServiceImpl(NewsArticleRepository newsArticleRepository, LemmaTypeService lemmaTypeService, StanfordCoreNLP pipeline) {
        this.newsArticleRepository = newsArticleRepository;
        this.lemmaTypeService = lemmaTypeService;
        this.pipeline = pipeline;
    }

    @Async
    public void saveNewsPaperArticle(CreateNewsPaperArticleDTO createNewsPaperArticleDTO) {
        NewsArticle newsArticle = NewsArticle
                .builder()
                .newsPaperName(createNewsPaperArticleDTO.getNewsPaperName())
                .title(createNewsPaperArticleDTO.getTitle())
                .publicationYear(createNewsPaperArticleDTO.getPublicationYear())
                .build();
        newsArticle.setSentences(createSentencesFromNewsPaperArticle(createNewsPaperArticleDTO.getText(), newsArticle));
        Set<LemmaType> lemmaTypes = createLemmaTypesFromSentencesForNewsArticle(newsArticle.getSentences());
        newsArticle.setLemmaTypes(lemmaTypes);
        newsArticle.setRelevance(setRelevanceForNewsArticleByLemmaTypes(lemmaTypes));
        log.info("NewsArticle with title {} and number of sentences {} saved", newsArticle.getTitle(), newsArticle.getSentences().size());
        newsArticleRepository.save(newsArticle);
    }

    private int setRelevanceForNewsArticleByLemmaTypes(Set<LemmaType> lemmaTypes) {
        final int[] relevance = {0};
        Set<String> keyWords = KeyWordsSingleton.getKeyWords();
        lemmaTypes.forEach(lemmaType -> {
            if (keyWords.contains(lemmaType.getText())) {
                relevance[0] += 1;
            }
        });
        return relevance[0];
    }

    private Set<LemmaType> createLemmaTypesFromSentencesForNewsArticle(List<Sentence> sentences) {
        return sentences
                .stream()
                .map(Sentence::getLemmaTypes)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private List<Sentence> createSentencesFromNewsPaperArticle(String text, NewsArticle newsArticle) {
        CoreDocument coreDocument = new CoreDocument(text);
        pipeline.annotate(coreDocument);
        List<CoreSentence> coreSentences = coreDocument.sentences();

        short[] position = {0};
        List<Sentence> sentences = new ArrayList<>();
                coreSentences.forEach(coreSentence -> {
                    Sentence sentence = Sentence
                            .builder()
                            .newsArticle(newsArticle)
                            .text(coreSentence.text())
                            .textPosition(position[0])
                            .build();
                    if (ErrorServiceImpl.invalidSentence(sentence.getText())) {
                        sentence.setInvalid(true);
                    }
                    sentence.setLemmaTypes(createLemmaTypesFromSentences(coreSentence, sentence, newsArticle));
                    sentence.setTextTokens(new ArrayList<>(filteredTextTokens));
                    filteredTextTokens.clear();
                    position[0]++;
                    sentences.add(sentence);
                });
        return sentences;
    }

    @Transactional
    protected Set<LemmaType> createLemmaTypesFromSentences(CoreSentence coreSentence, Sentence sentence, NewsArticle newsArticle) {
        List<CoreLabel> coreLabels = coreSentence.tokens();
        final short[] position = {0};
        final short[] coreLabelPosition = {0};

        Set<LemmaType> lemmaTypes = new HashSet<>();
                coreSentence.tokensAsStrings().stream()
                        .filter(word -> !word.replaceAll("[^0-9\\p{L}\\s]", "").isEmpty())
                        .forEach(word -> {
                            LemmaType lemmaType = null;
                            String phraseType = coreLabels.get(coreLabelPosition[0]).get(CoreAnnotations.PartOfSpeechAnnotation.class);
                            Set<LemmaType> lemmaTypesFromDatabase = lemmaTypeService.findAllByText(word);
                            TextToken textToken = TextToken
                                    .builder()
                                    .text(word)
                                    .sentencePosition(position[0])
                                    .sentence(sentence)
                                    .phraseType(phraseType)
                                    .build();
                            filteredTextTokens.add(textToken);
                            if (lemmaTypesFromDatabase.size() == 1) {
                                lemmaType = getLemmaTypeFromSet(word, lemmaTypesFromDatabase);
                                lemmaType.addOneTextToken(textToken);
                                lemmaType.addOneSentence(sentence);
                                lemmaType.addOneNewsArticle(newsArticle);
                                textToken.setLemmaType(lemmaType);
                            } else {
                                textToken.setInvalid(true);
                            }
                            position[0]++;
                            coreLabelPosition[0]++;
                            if (lemmaType != null){
                                lemmaTypes.add(lemmaType);
                            }
                        });
        return lemmaTypes;
    }

    private LemmaType getLemmaTypeFromSet(String word, Set<LemmaType> lemmaTypesFromDatabase) {
           return lemmaTypesFromDatabase.stream()
                   .reduce((first, second) -> first)
                   .orElse(null);
    }
}
