package kdodds.user_service.controllers;

import kdodds.user_service.errors.models.exceptions.InvalidUserDataException;
import kdodds.user_service.models.CompleteUserData;
import kdodds.user_service.models.responses.UserResponse;
import kdodds.user_service.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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

        CompleteUserData userData = userService.getCompleteUserDataByUserId(userId);

        return new ResponseEntity<>(
            UserResponse.builder()
                        .userId(userData.getUser().getId())
                        .username(userData.getUser().getUsername())
                        .email(userData.getUser().getEmail())
                        .createdAt(userData.getUser().getCreatedAt())
                        .updatedAt(userData.getUser().getUpdatedAt())
                        .firstName(userData.getUserProfile().getFirstName())
                        .lastName(userData.getUserProfile().getLastName())
                        .phoneNumber(userData.getUserProfile().getPhoneNumber())
                        .profileImageUrl(userData.getUserProfile().getProfileImageUrl())
                        .addresses(userData.getUserAddresses())
                        .build(),
            HttpStatus.OK
        );
    }

}
