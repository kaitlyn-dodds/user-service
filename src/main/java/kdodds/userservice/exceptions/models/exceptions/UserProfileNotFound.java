package kdodds.userservice.exceptions.models.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class UserProfileNotFound extends RuntimeException {

    /**
     * Constructor.
     *
     * @param userId The user id that was not found.
     */
    public UserProfileNotFound(String userId) {
        super("User profile not found for user id: " + userId);
    }

}
