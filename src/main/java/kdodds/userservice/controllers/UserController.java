package kdodds.userservice.controllers;

import kdodds.userservice.errors.models.exceptions.InvalidUserIdException;
import kdodds.userservice.models.CompleteUserData;
import kdodds.userservice.models.UserAddress;
import kdodds.userservice.models.UserProfile;
import kdodds.userservice.models.responses.UserAddressResponse;
import kdodds.userservice.models.responses.UserAddressesResponse;
import kdodds.userservice.models.responses.UserProfileResponse;
import kdodds.userservice.models.responses.UserResponse;
import kdodds.userservice.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Handles all user related endpoints.
 */

@RestController()
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    /**
     * Test endpoint.
     *
     * @return String
     */
    @GetMapping("/test")
    public String test() {
        return "test";
    }

    /**
     * Gets all user data for a given user id.
     *
     * @param userId The unique user id of the user
     * @return UserResponse wrapped in a ResponseEntity
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserByUserId(@PathVariable String userId) {
        // check for null or invalid user id
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
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
                .addresses(convertUserAddresses(userData.getUserAddresses()))
                .build(),
            HttpStatus.OK
        );
    }

    /**
     * Get user profile for a given user id.
     *
     * @param userId Unique user id of the user.
     * @return UserProfile object.
     */
    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileResponse> getUserProfileByUserId(@PathVariable String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
        }

        UserProfile userProfile = userService.getUserProfileByUserId(userId);

        return new ResponseEntity<>(
            UserProfileResponse.builder()
                .userId(userProfile.getUserId())
                .firstName(userProfile.getFirstName())
                .lastName(userProfile.getLastName())
                .phoneNumber(userProfile.getPhoneNumber())
                .profileImageUrl(userProfile.getProfileImageUrl())
                .createdAt(userProfile.getCreatedAt())
                .updatedAt(userProfile.getUpdatedAt())
                .build(),
            HttpStatus.OK
        );
    }

    /**
     * Get user addresses for a given user id.
     *
     * @param userId Unique user id of the user.
     * @return UserAddressesResponse object.
     */
    @GetMapping("/{userId}/addresses")
    public ResponseEntity<UserAddressesResponse> getUserAddressesByUserId(@PathVariable String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
        }

        List<UserAddress> addresses = userService.getUserAddressesByUserId(userId);

        // convert to response objects
        List<UserAddressResponse> addressResponses = convertUserAddresses(addresses);

        return new ResponseEntity<>(
            UserAddressesResponse.builder()
                .userId(userId)
                .addresses(addressResponses)
                .build(),
            HttpStatus.OK
        );
    }

    private List<UserAddressResponse> convertUserAddresses(List<UserAddress> addresses) {
        return addresses.stream()
            .map(UserAddress::convert)
            .toList();
    }

}
