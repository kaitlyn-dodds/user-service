package kdodds.userservice.exceptions.models.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidRequestDataException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message The error message.
     */
    public InvalidRequestDataException(String message) {
        super(message);
    }

}
