package kdodds.userservice.services;

import kdodds.userservice.dto.requests.CreateUserAddressRequestDto;
import kdodds.userservice.dto.requests.PatchUserAddressRequestDto;
import kdodds.userservice.dto.responses.UserAddressResponseDto;
import kdodds.userservice.dto.responses.UserAddressesResponseDto;
import kdodds.userservice.entities.User;
import kdodds.userservice.entities.UserAddress;
import kdodds.userservice.exceptions.models.exceptions.InvalidRequestDataException;
import kdodds.userservice.exceptions.models.exceptions.InvalidUserIdException;
import kdodds.userservice.exceptions.models.exceptions.UserAddressNotFound;
import kdodds.userservice.exceptions.models.exceptions.UserNotFoundException;
import kdodds.userservice.repositories.UserAddressRepository;
import kdodds.userservice.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserAddressService {

    private UserRepository userRepository;

    private UserAddressRepository userAddressRepository;

    /**
     * Gets a user address by address id. Returns the single UserAddress if found.
     *
     * @param userId The user id to use for the test data.
     * @param addressId The address id to use for the test data.
     * @return UserAddressResponseDto
     */
    public UserAddressResponseDto getUserAddressDtoById(String userId, String addressId) throws Exception {
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
                    "Find address by id for userId %s and addressId %s failed for unknown reasons", userId, addressId
                ),
                ex
            );
        }

        if (address.isEmpty()) {
            log.warn("User address not found for address id: {}, user id: {}", addressId, userId);
            throw new UserAddressNotFound(
                String.format(
                    "No user address found for userId %s and addressId %s",
                    userId,
                    addressId
                )
            );
        }

        return UserAddressResponseDto.fromEntity(address.get());
    }

    /**
     * Method to get all user addresses by user id. Returns a list of UserAddresses.
     *
     * @param userId User id to use to get the addresses.
     * @return UserAddressesResponseDto
     */
    public UserAddressesResponseDto getUserAddressesDtoByUserId(String userId) throws Exception {
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
            log.warn("No user addresses found for id: {}", userId);
            addresses = Optional.of(List.of()); // return empty list
        }

        return UserAddressesResponseDto.from(userId, addresses.get());
    }

    /**
     * Deletes a user address by address id.
     *
     * @param userId The user id of the user who owns the address.
     * @param addressId The address id of the address to delete.
     * @throws Exception Throws an exception if the address cannot be deleted.
     */
    public void deleteUserAddressByAddressId(String userId, String addressId) throws Exception {
        if (userId == null || userId.isEmpty()) {
            log.error("Cannot delete user address with null or empty user id.");
            throw new InvalidUserIdException();
        }

        if (addressId == null || addressId.isEmpty()) {
            log.error("Cannot delete user address with null or empty address id.");
            throw new InvalidRequestDataException("Invalid null or empty address id");
        }

        try {
            int rowsImpacted = userAddressRepository
                .deleteAddressById(UUID.fromString(userId), UUID.fromString(addressId));

            log.info("Deleted {} user address(es) for user id: {}", rowsImpacted, userId);
        } catch (Exception ex) {
            log.error("Error deleting user address for user id: {}, address id: {}", userId, addressId, ex);
            throw new Exception(
                String.format("Error deleting user address for user id: %s, address id: %s", userId, addressId)
            );
        }
    }

    /**
     * Creates a new user address. Returns the newly created user address as a UserAddressResponseDto.
     *
     * @param userId The user id of the user who will own the address.
     * @param request The CreateUserAddressRequestDto to use for the data.
     * @return UserAddressResponseDto
     * @throws Exception Throws an exception if the request is invalid or the attempt to create the user address fails.
     */
    public UserAddressResponseDto createUserAddress(String userId, CreateUserAddressRequestDto request)
        throws Exception {
        // userId should be valid UUID
        if (userId == null || userId.isEmpty()) {
            log.error("Cannot create address for null or empty userId.");
            throw new InvalidUserIdException();
        }

        // need to validate request data
        validateCreateUserAddressRequest(request);

        // get the user reference
        User user = userRepository.getReferenceById(UUID.fromString(userId));

        UserAddress address = new UserAddress();
        address.setUser(user);
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        address.setCountry(request.getCountry());
        address.setCreatedAt(Instant.now());
        address.setUpdatedAt(Instant.now());

        // not a required field, don't overwrite the default unless a value is provided
        if (request.getAddressType() != null && !request.getAddressType().isEmpty()) {
            address.setAddressType(request.getAddressType());
        }

        try {
            address = userAddressRepository.saveAndFlush(address);

            return UserAddressResponseDto.fromEntity(address);
        } catch (DataIntegrityViolationException ex) {
            if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException cve) {
                // check for foreign key constraint
                if (cve.getMessage().contains("user_addresses_user_id_fkey")) {
                    log.error("User with id {} does not exist", userId);
                    throw new UserNotFoundException(userId);
                }
            }

            log.error("Error creating user address due to data integrity violation: {}", ex.getMessage());
            throw new Exception(String.format("Error creating user address for user id: %s", userId), ex);
        } catch (Exception ex) {
            log.error(
                "Error creating user address for user id: {} - {}",
                userId,
                ex.getMessage()
            );
            throw new Exception(String.format("Error creating user address for user id: %s", userId), ex);
        }
    }

    /**
     * Updates a user address by address id.
     *
     * @param userId The user id of the user who owns the address.
     * @param addressId The address id of the address to update.
     * @param request The PatchUserAddressRequestDto to use for the data.
     * @return UserAddressResponseDto
     * @throws Exception Throws an exception if the request is invalid or the attempt to update the user address fails.
     */
    public UserAddressResponseDto updateUserAddressById(
        String userId,
        String addressId,
        PatchUserAddressRequestDto request
    ) throws Exception {
        if (userId == null || userId.isEmpty()) {
            log.error("Cannot update user address with null or empty userId.");
            throw new InvalidUserIdException();
        }

        if (addressId == null || addressId.isEmpty()) {
            log.error("Cannot update user address with null or empty address id.");
            throw new InvalidRequestDataException("Invalid null or empty address id");
        }

        if (request == null) {
            log.error("Request body must be included in Patch User Address request");
            throw new InvalidRequestDataException("Request body must be included in Patch User Address request");
        }

        // find the existing user address (throw if not found)
        UserAddress address = userAddressRepository.findById(UUID.fromString(addressId))
            .orElseThrow(() -> new UserAddressNotFound(
                String.format("No user address found for address id %s", addressId)
            ));

        // need to validate that the address belongs to the user
        if (!address.getUser().getId().equals(UUID.fromString(userId))) {
            log.error("User address does not belong to user with id: {}", userId);
            throw new UserAddressNotFound(
                String.format("No user address found for address id %s", addressId)
            );
        }

        // apply the updates
        boolean updateNeeded = applyUpdates(request, address);

        if (!updateNeeded) {
            log.info("No changes detected for update user address with id: {}", addressId);
            return UserAddressResponseDto.fromEntity(address);
        }

        // update required, set the updated at timestamp
        address.setUpdatedAt(Instant.now());

        try {
            address = userAddressRepository.save(address);
        } catch (Exception ex) {
            log.error("Error updating user address for user id: {}, address id: {}", userId, addressId, ex);
            throw new Exception(
                String.format("Error updating user address for user id: %s, address id: %s", userId, addressId)
            );
        }

        return UserAddressResponseDto.fromEntity(address);
    }

    private boolean applyUpdates(PatchUserAddressRequestDto request, UserAddress address) {
        boolean updateNeeded = false;

        // for all properties; only apply the update if the value is not null, empty, or the same as the existing value
        if (request.getAddressType() != null
            && !request.getAddressType().equals(address.getAddressType())) {
            if (request.getAddressType().isEmpty()) {
                throw new InvalidRequestDataException("Address type cannot be empty");
            }
            address.setAddressType(request.getAddressType());
            updateNeeded = true;
        }

        if (request.getAddressLine1() != null
            && !request.getAddressLine1().equals(address.getAddressLine1())) {
            if (request.getAddressLine1().isEmpty()) {
                throw new InvalidRequestDataException("Address line 1 cannot be empty");
            }
            address.setAddressLine1(request.getAddressLine1());
            updateNeeded = true;
        }

        // address line 2 can be set to empty (if value is empty, set to null on entity)
        if (request.getAddressLine2() != null
            && !request.getAddressLine2().equals(address.getAddressLine2())) {
            if (request.getAddressLine2().isEmpty()) {
                address.setAddressLine2(null);
            } else {
                address.setAddressLine2(request.getAddressLine2());
            }
            updateNeeded = true;
        }

        if (request.getCity() != null
            && !request.getCity().equals(address.getCity())) {
            if (request.getCity().isEmpty()) {
                throw new InvalidRequestDataException("City cannot be empty");
            }
            address.setCity(request.getCity());
            updateNeeded = true;
        }

        if (request.getState() != null
            && !request.getState().equals(address.getState())) {
            if (request.getState().isEmpty()) {
                throw new InvalidRequestDataException("State cannot be empty");
            }
            address.setState(request.getState());
            updateNeeded = true;
        }

        if (request.getZipCode() != null
            && !request.getZipCode().equals(address.getZipCode())) {
            if (request.getZipCode().isEmpty()) {
                throw new InvalidRequestDataException("Zip code cannot be empty");
            }
            address.setZipCode(request.getZipCode());
            updateNeeded = true;
        }

        if (request.getCountry() != null
            && !request.getCountry().equals(address.getCountry())) {
            if (request.getCountry().isEmpty()) {
                throw new InvalidRequestDataException("Country cannot be empty");
            }
            address.setCountry(request.getCountry());
            updateNeeded = true;
        }

        return updateNeeded;
    }

    private void validateCreateUserAddressRequest(CreateUserAddressRequestDto request) {
        if (request == null) {
            log.error("Request body must be included in Create User Address request");
            throw new InvalidRequestDataException("Request body must be included in Create User Address request");
        }

        if (request.getAddressLine1() == null || request.getAddressLine1().isEmpty()) {
            log.error("Address line 1 must be included in create user address request");
            throw new InvalidRequestDataException("Address line 1 must be included in create user address request");
        }

        if (request.getCity() == null || request.getCity().isEmpty()) {
            log.error("City must be included in create user address request");
            throw new InvalidRequestDataException("City must be included in create user address request");
        }

        if (request.getState() == null || request.getState().isEmpty()) {
            log.error("State must be included in create user address request");
            throw new InvalidRequestDataException("State must be included in create user address request");
        }

        if (request.getZipCode() == null || request.getZipCode().isEmpty()) {
            log.error("Zip code must be included in create user address request");
            throw new InvalidRequestDataException("Zip code must be included in create user address request");
        }

        if (request.getCountry() == null || request.getCountry().isEmpty()) {
            log.error("Country must be included in create user address request");
            throw new InvalidRequestDataException("Country must be included in create user address request");
        }
    }
}
