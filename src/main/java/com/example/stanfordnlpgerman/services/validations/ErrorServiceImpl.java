package com.example.stanfordnlpgerman.services.validations;


import com.example.stanfordnlpgerman.exceptions.validations.MissingParamsException;
import com.example.stanfordnlpgerman.models.dtos.validations.ErrorMessageDTO;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ErrorServiceImpl {

  public static void buildMissingFieldErrorMessage(Object object) throws MissingParamsException {
    if (object == null) {
      throw new NullPointerException("Object to be verified for null or empty fields is null");
    }
    StringBuilder result = new StringBuilder();
    List<String> missingFields = checkInputNullFields(object);
    StringBuilder errorMessage = new StringBuilder();
    for (String word : missingFields) {
      result.append(word)
              .append(", ");
    }
    if (result.length() != 0) {
      errorMessage.
              append(result.substring(0, 1).toUpperCase())
              .append(result.substring(1, result.length() - 2))
              .append(" ")
              .append( "is required.");
      log.error(errorMessage.toString());
      throw new MissingParamsException(errorMessage.toString());
    }
  }

  private static List<String> checkInputNullFields(Object object) {
    List<String> missingFields = new ArrayList<>();
    for (Field field : object.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      try {
        if (field.get(object) == null || field.get(object).equals("")) {
          missingFields.add(field.getName());
        }
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    return missingFields;
  }

  public static boolean invalidSentence(String sentence) {
    return sentence.length() < 8 || sentence.matches("^[a-z].*");
  }
}
