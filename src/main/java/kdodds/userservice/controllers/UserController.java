package kdodds.userservice.controllers;

import kdodds.userservice.errors.models.exceptions.InvalidUserIdException;
import kdodds.userservice.dto.responses.UserAddressesResponseDto;
import kdodds.userservice.dto.responses.UserProfileResponseDto;
import kdodds.userservice.dto.responses.UserResponseDto;
import kdodds.userservice.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<UserResponseDto> getUserByUserId(@PathVariable String userId) {
        // check for null or invalid user id
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
        }

        UserResponseDto response = userService.getUserResponse(userId);

        return new ResponseEntity<>(
            response,
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
    public ResponseEntity<UserProfileResponseDto> getUserProfileByUserId(@PathVariable String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
        }

        UserProfileResponseDto userProfileResponseDto = userService.getUserProfileByUserId(userId);

        return new ResponseEntity<>(
            userProfileResponseDto,
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
    public ResponseEntity<UserAddressesResponseDto> getUserAddressesByUserId(@PathVariable String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
        }

        UserAddressesResponseDto addressesResponseDto = userService.getUserAddressesByUserId(userId);

        return new ResponseEntity<>(
            addressesResponseDto,
            HttpStatus.OK
        );
    }
}
