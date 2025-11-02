package kdodds.userservice.exceptions.models.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class UserConflictException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message The error message.
     */
    public UserConflictException(String message) {
        super(message);
    }

}
