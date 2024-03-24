package co.uk.yapily.validation.annotation;

import co.uk.yapily.validation.LabelsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LabelsValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLabels {

  String message() default "Invalid label";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}