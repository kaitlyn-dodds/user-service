package kdodds.user_service.errors;

import kdodds.user_service.errors.models.ErrorResponse;
import kdodds.user_service.errors.models.exceptions.InvalidUserDataException;
import kdodds.user_service.errors.models.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {

    /**
     * Handle UserNotFoundExceptions, thrown when a user cannot be found for some given data.
     *
     * @param ex The UserNotFoundException object.
     * @return An ErrorResponse wrapped in a ResponseEntity.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
                                           .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                                           .message(ex.getMessage())
                                           .status(HttpStatus.NOT_FOUND.value())
                                           .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle InvalidUserDataException exceptions, thrown when some provided user data is invalid and cannot be used.
     *
     * @param ex The InvalidUserDataException object.
     * @return An ErrorResponse wrapped in a ResponseEntity.
     */
    @ExceptionHandler(InvalidUserDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUserDataException(InvalidUserDataException ex) {
        ErrorResponse response = ErrorResponse.builder()
                                              .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                              .message(ex.getMessage())
                                              .status(HttpStatus.BAD_REQUEST.value())
                                              .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle generic exceptions.
     *
     * @param ex The Exception object.
     * @return An ErrorResponse wrapped in a ResponseEntity.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse response = ErrorResponse.builder()
                                              .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                                              .message(ex.getMessage())
                                              .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                              .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
