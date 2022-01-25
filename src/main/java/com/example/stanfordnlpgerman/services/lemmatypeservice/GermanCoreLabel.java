package com.example.stanfordnlpgerman.services.lemmatypeservice;

import edu.stanford.nlp.ling.CoreLabel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GermanCoreLabel extends CoreLabel {
  private static final List<String> lemmaFileContent = readLemmaFile();

  public GermanCoreLabel() {
  }

  public GermanCoreLabel(CoreLabel coreLabel) {
    super(coreLabel);
  }

  private static List<String> readLemmaFile() {
    Path path = Paths.get("src/main/resources/static/lemma/lemmatization-de.txt");
    try {
      return Files.readAllLines(path);
    } catch (IOException e) {
      System.out.println("Couldn't read file");
    }
    return new ArrayList<>();
  }

  @Override
  public String lemma() {
    return lemmaFileContent
            .stream()
            .filter(lemma ->
                    lemma.split("\\s+")[1].equalsIgnoreCase(this.originalText())
                            || lemma.split("\\s+")[0].equalsIgnoreCase(this.originalText()))
            .map(lemma -> lemma.split("\\s+")[0])
            .findFirst()
            .orElse(null);
  }

  public static List<String> getFileContent() {
    return lemmaFileContent;
  }
}
