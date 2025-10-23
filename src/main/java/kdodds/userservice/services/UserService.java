package kdodds.userservice.services;

import kdodds.userservice.errors.models.exceptions.InvalidUserIdException;
import kdodds.userservice.errors.models.exceptions.UserNotFoundException;
import kdodds.userservice.models.CompleteUserData;
import kdodds.userservice.models.User;
import kdodds.userservice.models.UserAddress;
import kdodds.userservice.models.UserProfile;
import kdodds.userservice.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    /**
     * Gets all user data for a given user id.
     *
     * @param userId The unique user id of the user.
     * @return A CompleteUserData object.
     */
    public CompleteUserData getCompleteUserDataByUserId(String userId) {
        // TODO: would query db w/ single query w/ joins for user, profile, addresses

        return CompleteUserData.builder()
            .user(getUserByUserId(userId))
            .userProfile(getUserProfileByUserId(userId))
            .userAddresses(getUserAddressesByUserId(userId))
            .build();
    }

    /**
     * User data for a given user id. Will throw an exception for a null or missing user id.
     *
     * @param userId Unique user id of the user.
     * @return A User object.
     * @throws UserNotFoundException Thrown when invalid user id is provided.
     */
    public User getUserByUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
        }

        // reach out to database to get user data
        try {
            userRepository.findById(UUID.fromString(userId));
        } catch (Exception ex) {
            log.error("Unable to find user with id: {}", userId);
            log.error("Exception: {}", ex.getMessage());
            throw new UserNotFoundException(userId);
        }

        return User.builder()
            .id(userId)
            .username("magicalwizardman4848")
            .email("somewhere@someplace.com")
            .status("active")
            .passwordHash("password")
            .updatedAt(Instant.now())
            .createdAt(Instant.now())
            .build();
    }

    /**
     * Gets a users profile given a user id. Returns the UserProfile if found.
     *
     * @param userId User id to get the profile.
     * @return UserProfile
     */
    public UserProfile getUserProfileByUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
        }

        return UserProfile.builder()
            .userId(userId)
            .firstName("Tom")
            .lastName("Bombadil")
            .phoneNumber("5746857273733")
            .build();
    }

    /**
     * Method to get all user addresses by user id. Returns a list of UserAddresses.
     *
     * @param userId User id to use to get the addresses.
     * @return List of UserAddresses.
     */
    public List<UserAddress> getUserAddressesByUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
        }

        return List.of(
            UserAddress.builder()
                .userId(userId)
                .addressLine1("1717 Old Forest Rd")
                .addressType("Home")
                .city("Old Forest")
                .state("Old Forest")
                .country("Middle Earth")
                .zipCode("12345")
                .build()
        );
    }

}
