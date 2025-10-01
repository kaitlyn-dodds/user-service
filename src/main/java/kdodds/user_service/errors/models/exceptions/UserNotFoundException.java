package kdodds.user_service.errors.models.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param userId The user id that was not found.
     */
    public UserNotFoundException(String userId) {
        super("User with id " + userId + " not found");
    }
}

