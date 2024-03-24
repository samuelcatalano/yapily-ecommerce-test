package co.uk.yapily.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A converter class that converts a List of Strings to a String and vice versa, using a semicolon as the separator.
 * The semicolon is used to ensure that the String can be parsed back into a List of Strings even if the input String contains semicolons.
 *
 * @author Samuel Catalano
 * @since 1.0.0
 */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

  /**
   * The separator character used to split the input String into a List of Strings.
   */
  private static final String SPLIT_CHAR = ",";

  /**
   * Converts a List of Strings to a String, separated by semicolons. If the input List is null, an empty String is returned.
   *
   * @param stringList the List of Strings to be converted
   * @return the input List of Strings separated by semicolons, or an empty String if the input List is null
   */
  @Override
  public String convertToDatabaseColumn(final List<String> stringList) {
    return stringList != null ? String.join(SPLIT_CHAR, stringList) : "";
  }

  /**
   * Converts a String back into a List of Strings, by splitting the input String on semicolons. If the input String is null, an empty List is returned.
   *
   * @param string the input String to be converted
   * @return the input String split on semicolons, or an empty List if the input String is null
   */
  @Override
  public List<String> convertToEntityAttribute(final String string) {
    return string != null ? Arrays.asList(string.split(SPLIT_CHAR)) : Collections.emptyList();
  }
}
