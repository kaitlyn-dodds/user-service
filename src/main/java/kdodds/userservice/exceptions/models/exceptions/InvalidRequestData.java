package kdodds.userservice.exceptions.models.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidRequestData extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message The error message.
     */
    public InvalidRequestData(String message) {
        super(message);
    }

}
