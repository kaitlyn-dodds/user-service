package kdodds.userservice.services;

import kdodds.userservice.errors.models.exceptions.InvalidUserIdException;
import kdodds.userservice.dto.responses.UserAddressesResponseDto;
import kdodds.userservice.dto.responses.UserProfileResponseDto;
import kdodds.userservice.dto.responses.UserResponseDto;
import kdodds.userservice.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    /**
     * Gets all user data for a given user id.
     *
     * @param userId The unique user id of the user.
     * @return UserResponseDto
     */
    public UserResponseDto getUserResponse(String userId) {
        // TODO: would query db w/ single query w/ joins for user, profile, addresses

        return UserResponseDto.builder()
            .userId(userId)
            .build();
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

        return UserProfileResponseDto.builder().build();
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

        return UserAddressesResponseDto.builder().build();
    }

}
