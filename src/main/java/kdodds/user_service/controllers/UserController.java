package kdodds.user_service.controllers;

import kdodds.user_service.errors.models.exceptions.InvalidUserDataException;
import kdodds.user_service.models.User;
import kdodds.user_service.models.UserAddress;
import kdodds.user_service.models.UserProfile;
import kdodds.user_service.models.responses.UserResponse;
import kdodds.user_service.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Handles all user related endpoints.
 */

@RestController("")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    /**
     * Gets all user data for a given user id.
     *
     * @param userId The unique user id of the user
     * @return UserResponse wrapped in a ResponseEntity
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUserByUserId(@PathVariable String userId) {
        // check for null or invalid user id
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserDataException();
        }

        User user = userService.getUserByUserId(userId);
        UserProfile profile = userService.getUserProfileByUserId(userId);
        List<UserAddress> userAddresses = userService.getUserAddressesByUserId(userId);

        return new ResponseEntity<>(
            UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .updatedAt(user.getUpdatedAt())
                .createdAt(user.getCreatedAt())
                .addresses(userAddresses)
                .userProfile(profile)
                .build(),
            HttpStatus.OK
        );
    }

}
