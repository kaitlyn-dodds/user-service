package kdodds.userservice.services;

import kdodds.userservice.dto.responses.UserAddressesResponseDto;
import kdodds.userservice.dto.responses.UserProfileResponseDto;
import kdodds.userservice.dto.responses.UserResponseDto;
import kdodds.userservice.entities.User;
import kdodds.userservice.entities.UserProfile;
import kdodds.userservice.errors.models.exceptions.InvalidUserIdException;
import kdodds.userservice.errors.models.exceptions.UserNotFoundException;
import kdodds.userservice.repositories.UserAddressRepository;
import kdodds.userservice.repositories.UserProfileRepository;
import kdodds.userservice.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    private UserProfileRepository userProfileRepository;

    private UserAddressRepository userAddressRepository;

    /**
     * Gets all user data for a given user id.
     *
     * @param userId The unique user id of the user.
     * @return UserResponseDto
     */
    public UserResponseDto getUserResponse(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
        }

        Optional<User> user;
        try {
            user = userRepository.findById(UUID.fromString(userId));
        } catch (Exception ex) {
            log.error("Error getting user for user id: {}", userId, ex);
            throw new InvalidUserIdException();
        }

        if (user.isEmpty()) {
            log.warn("User not found for id: {}", userId);
            throw new UserNotFoundException("User not found for id: " + userId);
        }

        return UserResponseDto.fromEntity(user.get());
    }

    /**
     * Gets a users profile given a user id. Returns the UserProfile if found.
     *
     * @param userId User id to get the profile.
     * @return UserProfileResponseDto
     */
    public UserProfileResponseDto getUserProfileByUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
        }

        Optional<UserProfile> profile;
        try {
            profile = userProfileRepository.findById(UUID.fromString(userId));
        } catch (Exception ex) {
            log.error("Error getting user profile for user id: {}", userId, ex);
            throw new InvalidUserIdException();
        }

        if (profile.isEmpty()) {
            log.warn("User profile not found for id: {}", userId);
            throw new UserNotFoundException("User profile not found for id: " + userId);
        }

        return UserProfileResponseDto.fromEntity(profile.get());
    }

    /**
     * Method to get all user addresses by user id. Returns a list of UserAddresses.
     *
     * @param userId User id to use to get the addresses.
     * @return UserAddressesResponseDto
     */
    public UserAddressesResponseDto getUserAddressesByUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
        }

        // TODO: find all by user id

        return UserAddressesResponseDto.builder().build();
    }

}
