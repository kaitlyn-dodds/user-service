package kdodds.userservice.exceptions;

import kdodds.userservice.exceptions.models.ErrorResponse;
import kdodds.userservice.exceptions.models.exceptions.InvalidRequestDataException;
import kdodds.userservice.exceptions.models.exceptions.InvalidUserIdException;
import kdodds.userservice.exceptions.models.exceptions.UserAddressNotFound;
import kdodds.userservice.exceptions.models.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {

    /**
     * Handles "Not Found" exceptions, thrown when a requested resource is not found.
     *
     * @param ex The RuntimeException object.
     * @return An ErrorResponse wrapped in a ResponseEntity.
     */
    @ExceptionHandler({UserNotFoundException.class, UserAddressNotFound.class})
    public ResponseEntity<ErrorResponse> handleDataNotFound(RuntimeException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .error(HttpStatus.NOT_FOUND.getReasonPhrase())
            .message(ex.getMessage())
            .status(HttpStatus.NOT_FOUND.value())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle invalid request data related exceptions, thrown when some provided user data is invalid and cannot be
     * used.
     *
     * @param ex The RuntimeException object.
     * @return An ErrorResponse wrapped in a ResponseEntity.
     */
    @ExceptionHandler({InvalidUserIdException.class, InvalidRequestDataException.class})
    public ResponseEntity<ErrorResponse> handleInvalidRequestDataException(RuntimeException ex) {
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
