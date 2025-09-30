package kdodds.user_service.controllers;

import kdodds.user_service.models.User;
import kdodds.user_service.models.responses.UserResponse;
import kdodds.user_service.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController("/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    /**
     * Gets all user data for a given user id.
     *
     * @param userId The unique user id of the user
     * @return UserResponse
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserByUserId(@PathVariable String userId) {
        // check for null or invalid user id
        if (userId == null || userId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User user;
        try {
            user = userService.getUserByUserId(userId);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(
            new UserResponse(user, null),
            HttpStatus.OK
        );
    }

}
