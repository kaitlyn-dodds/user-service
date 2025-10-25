package kdodds.userservice.exceptions.models.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserAddressNotFound extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message The error message.
     */
    public UserAddressNotFound(String message) {
        super(message);
    }

}
