package kdodds.user_service.errors.models.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidUserDataException extends RuntimeException {

    /**
     * Constructor.
     */
    public InvalidUserDataException() {
        super("Invalid null or empty user id");
    }
}
