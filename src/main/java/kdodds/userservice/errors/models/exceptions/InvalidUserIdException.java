package kdodds.userservice.errors.models.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidUserIdException extends RuntimeException {

    /**
     * Constructor.
     */
    public InvalidUserIdException() {
        super("Invalid null or empty user id");
    }

}
