package kdodds.user_service.services;

import kdodds.user_service.errors.models.exceptions.InvalidUserDataException;
import kdodds.user_service.errors.models.exceptions.UserNotFoundException;
import kdodds.user_service.models.User;
import kdodds.user_service.models.UserAddress;
import kdodds.user_service.models.UserProfile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserService {

    /**
     * User data for a given user id. Will throw an exception for a null or missing user id.
     *
     * @param userId Unique user id of the user.
     * @return A User object.
     * @throws UserNotFoundException Thrown when invalid user id is provided.
     */
    public User getUserByUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserDataException();
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
            throw new InvalidUserDataException();
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
            throw new InvalidUserDataException();
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
