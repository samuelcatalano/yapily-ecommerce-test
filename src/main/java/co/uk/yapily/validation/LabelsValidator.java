package co.uk.yapily.validation;

import co.uk.yapily.validation.annotation.ValidLabels;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

/**
 * A class that implements the ConstraintValidator interface for validating a list of strings against a set of valid labels.
 *
 * @author Samuel Catalano
 * @since 1.0.0
 */
public class LabelsValidator implements ConstraintValidator<ValidLabels, List<String>> {

  /**
   * A list of valid labels that can be used in a list of strings.
   */
  private static final List<String> VALID_LABELS = Arrays.asList("drink", "food", "clothes", "limited");

  /**
   * This method is used to initialize the validator with the constraint annotation.
   * @param constraintAnnotation the constraint annotation that is used to initialize the validator
   */
  @Override
  public void initialize(final ValidLabels constraintAnnotation) {
    // do nothing
  }

  /**
   * This method is used to validate a list of strings against the set of valid labels.
   *
   * @param labels  the list of strings to be validated
   * @param context the context of the validation process
   * @return true if the list of strings contains only valid labels, false otherwise
   */
  @Override
  public boolean isValid(final List<String> labels, final ConstraintValidatorContext context) {
    return labels.stream().allMatch(VALID_LABELS::contains);
  }
}
