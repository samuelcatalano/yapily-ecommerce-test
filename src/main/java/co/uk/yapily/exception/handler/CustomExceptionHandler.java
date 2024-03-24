package co.uk.yapily.exception.handler;

import co.uk.yapily.dto.exception.ErrorResponse;
import co.uk.yapily.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler {

  /**
   * Handle {@link ApiException} exceptions thrown by the application and return an error response
   * with HTTP status code 500 (INTERNAL_SERVER_ERROR).
   *
   * @param ex the exception to handle
   * @param request the current request
   * @return an error response with HTTP status code 500 (INTERNAL_SERVER_ERROR)
   * @ExceptionHandler Specifies the type of exception this method handles.
   */
  @ExceptionHandler(value = ApiException.class)
  public ResponseEntity<Object> handleServiceException(final ApiException ex, final WebRequest request) {
    final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.name(), ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(errorResponse);
  }

  /**
   * Handles MethodArgumentNotValidException by returning a map of field errors.
   * This method is annotated with @ExceptionHandler to handle exceptions of type MethodArgumentNotValidException,
   * and it returns a map containing field names as keys and corresponding error messages as values.
   *
   * @param ex The MethodArgumentNotValidException instance to handle.
   * @return A map containing field names as keys and corresponding error messages as values.
   */
  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleValidationExceptions(final MethodArgumentNotValidException ex) {
    final Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach((error) -> {
      final String fieldName = error.getField();
      final String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return ResponseEntity.badRequest().body(errors);
  }
}
