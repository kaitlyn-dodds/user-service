package kdodds.userservice.services;

import kdodds.userservice.dto.responses.UserAddressResponseDto;
import kdodds.userservice.dto.responses.UserAddressesResponseDto;
import kdodds.userservice.dto.responses.UserProfileResponseDto;
import kdodds.userservice.dto.responses.UserResponseDto;
import kdodds.userservice.entities.User;
import kdodds.userservice.entities.UserAddress;
import kdodds.userservice.entities.UserProfile;
import kdodds.userservice.exceptions.models.exceptions.InvalidRequestDataException;
import kdodds.userservice.exceptions.models.exceptions.InvalidUserIdException;
import kdodds.userservice.exceptions.models.exceptions.UserAddressNotFound;
import kdodds.userservice.exceptions.models.exceptions.UserNotFoundException;
import kdodds.userservice.repositories.UserAddressRepository;
import kdodds.userservice.repositories.UserProfileRepository;
import kdodds.userservice.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public UserResponseDto getUserResponse(String userId) throws Exception {
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
        }

        Optional<User> user;
        try {
            user = userRepository.findById(UUID.fromString(userId));
        } catch (Exception ex) {
            log.error("Error getting user for user id: {}", userId, ex);
            throw new Exception(
                String.format("Find user by id for user id %s failed for unknown reasons", userId), ex
            );
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
    public UserProfileResponseDto getUserProfileByUserId(String userId) throws Exception {
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
        }

        Optional<UserProfile> profile;
        try {
            profile = userProfileRepository.findById(UUID.fromString(userId));
        } catch (Exception ex) {
            log.error("Error getting user profile for user id: {}", userId, ex);
            throw new Exception(
                String.format("Find profile by id for userId %s failed for unknown reasons", userId),
                ex
            );
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
    public UserAddressesResponseDto getUserAddressesByUserId(String userId) throws Exception {
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
        }

        Optional<List<UserAddress>> addresses;
        try {
            addresses = userAddressRepository.findAddressesByUserId(UUID.fromString(userId));
        } catch (Exception ex) {
            log.error("Error getting user addresses for user id: {}", userId, ex);
            throw new Exception(
                String.format("Find addresses by user id for userId %s failed for unknown reasons", userId),
                ex
            );
        }

        if (addresses.isEmpty()) {
            log.warn("User addresses not found for id: {}", userId);
            throw new UserAddressNotFound("User addresses not found for id: " + userId);
        }

        return UserAddressesResponseDto.from(userId, addresses.get());
    }

    /**
     * Gets a user address by address id. Returns the single UserAddress if found.
     *
     * @param userId The user id to use for the test data.
     * @param addressId The address id to use for the test data.
     * @return UserAddressResponseDto
     */
    public UserAddressResponseDto getUserAddressById(String userId, String addressId) throws Exception {
        // both ids should be valid UUIDs
        if (userId == null || userId.isEmpty() || addressId == null || addressId.isEmpty()) {
            throw new InvalidRequestDataException("Invalid request data.");
        }

        Optional<UserAddress> address;
        try {
            address = userAddressRepository.findById(UUID.fromString(addressId));
        } catch (Exception ex) {
            log.error("Error getting user address for user id: {}, address id: {}", userId, addressId, ex);
            throw new Exception(
                String.format(
                    "Find address by id for address id %s, user id %s, failed for unknown reasons", addressId, userId
                ),
                ex
            );
        }

        if (address.isEmpty()) {
            log.warn("User address not found for address id: {}", addressId);
            throw new UserAddressNotFound(
                String.format("Address not found for address id %s", addressId)
            );
        }

        return UserAddressResponseDto.fromEntity(address.get());
    }

}
