package kdodds.userservice.services;

import kdodds.userservice.dto.requests.CreateUserRequestDto;
import kdodds.userservice.dto.requests.UserAddressRequestDto;
import kdodds.userservice.dto.responses.PageDto;
import kdodds.userservice.dto.responses.PagedUsersResponseDto;
import kdodds.userservice.dto.responses.UserAddressResponseDto;
import kdodds.userservice.dto.responses.UserAddressesResponseDto;
import kdodds.userservice.dto.responses.UserProfileResponseDto;
import kdodds.userservice.dto.responses.UserResponseDto;
import kdodds.userservice.entities.User;
import kdodds.userservice.entities.UserAddress;
import kdodds.userservice.entities.UserProfile;
import kdodds.userservice.exceptions.models.exceptions.UserConflictException;
import kdodds.userservice.exceptions.models.exceptions.InvalidRequestDataException;
import kdodds.userservice.exceptions.models.exceptions.InvalidUserIdException;
import kdodds.userservice.exceptions.models.exceptions.UserAddressNotFound;
import kdodds.userservice.exceptions.models.exceptions.UserNotFoundException;
import kdodds.userservice.exceptions.models.exceptions.UserProfileNotFound;
import kdodds.userservice.repositories.UserAddressRepository;
import kdodds.userservice.repositories.UserProfileRepository;
import kdodds.userservice.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
     * Gets all users, paginated.
     */
    public PagedUsersResponseDto getAllUsersPaginated(int page, int size) {
        // create Pageable objects from provided page and size
        Pageable pageable = PageRequest.of(page, size);

        // get all users from the repository
        Page<User> userPage;
        try {
            userPage = userRepository.findAll(pageable);
        } catch (Exception ex) {
            log.error("Error getting all users paged: {}", ex.getMessage());
            throw new RuntimeException(ex);
        }

        // convert users in page to dtos
        List<UserResponseDto> userDtos = userPage
            .getContent()
            .stream()
            .map(UserResponseDto::fromEntity)
            .toList();

        // build paged dto
        PageDto pageDto = PageDto.builder()
            .page(userPage.getNumber())
            .size(userPage.getSize())
            .totalPages(userPage.getTotalPages())
            .totalElements(userPage.getTotalElements())
            .build();

        return PagedUsersResponseDto.builder()
            .users(userDtos)
            .page(pageDto)
            .build();
    }

    /**
     * Gets all user data for a given user id.
     *
     * @param userId The unique user id of the user.
     * @return UserResponseDto
     */
    public UserResponseDto getUserResponseDto(String userId) throws Exception {
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
            throw new UserNotFoundException(userId);
        }

        return UserResponseDto.fromEntity(user.get());
    }

    /**
     * Gets a users profile given a user id. Returns the UserProfile if found.
     *
     * @param userId User id to get the profile.
     * @return UserProfileResponseDto
     */
    public UserProfileResponseDto getUserProfileDtoByUserId(String userId) throws Exception {
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
        }

        Optional<UserProfile> profile;
        try {
            profile = userProfileRepository.findById(UUID.fromString(userId));
        } catch (Exception ex) {
            log.error("Error getting user profile for user id: {}", userId, ex);
            throw new Exception(
                String.format("Find user profile by id for userId %s failed for unknown reasons", userId),
                ex
            );
        }

        if (profile.isEmpty()) {
            log.warn("User profile not found for id: {}", userId);
            throw new UserProfileNotFound(userId);
        }

        return UserProfileResponseDto.fromEntity(profile.get());
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
                    "Find address by id for userId %s and addressId %s failed for unknown reasons",
                    userId,
                    addressId
                )
            );
        }

        return UserAddressResponseDto.fromEntity(address.get());
    }

    /**
     * Creates a new user. Returns the newly created user as a UserResponseDto.
     */
    public UserResponseDto createUser(CreateUserRequestDto request) throws Exception {
        // attempt to create and save the user (need to generate the id)
        Optional<User> user = createAndSaveUserFromCreateRequest(request);

        // throw and exit if no user was created
        if (user.isEmpty()) {
            log.warn("Cannot create user from create user request");
            throw new InvalidRequestDataException("Cannot create user from request data");
        }

        createAndSaveUserProfileFromCreateRequest(request, user.get());

        // create the user address
        if (request.getAddress() != null) {
            createAndSaveUserAddressFromRequest(request.getAddress(), user.get());
        }

        return UserResponseDto.fromEntity(user.get());
    }

    private Optional<User> createAndSaveUserFromCreateRequest(CreateUserRequestDto request) throws Exception {
        if (request == null) {
            log.warn("Cannot create user from null request");
            throw new InvalidRequestDataException("Cannot create user from null or empty request");
        }

        // create new user entity
        User user = composeUserEntityFromRequest(request);

        // save the user (get back the id right away)
        try {
            user = userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            String exceptionMessage = String.format(
                "Unknown error creating user with username: %s, email %s",
                user.getUsername(),
                user.getEmail()
            );

            // check if due to uniqueness violation
            if (ex.getCause() instanceof  org.hibernate.exception.ConstraintViolationException cve) {
                if (cve.getMessage().contains("users_username_key")) {
                    exceptionMessage = String.format("User with username %s already exists", user.getUsername());
                    throw new UserConflictException(exceptionMessage);
                }
                if (cve.getMessage().contains("email")) {
                    exceptionMessage = String.format("User with email %s already exists", user.getEmail());
                }
                throw new UserConflictException(exceptionMessage);
            }

            log.error(exceptionMessage);
            throw new Exception(exceptionMessage, ex);
        } catch (Exception ex) {
            log.error("Error creating new user: {} - {}", ex.getClass(), ex.getMessage());
            throw new Exception("Error creating new user", ex);
        }

        return Optional.of(user);
    }

    private Optional<UserProfile> createAndSaveUserProfileFromCreateRequest(CreateUserRequestDto request, User user)
        throws Exception {
        if (request == null || user == null) {
            log.warn("Cannot create user profile from null request or user");
            throw new InvalidRequestDataException("Cannot create user profile from null or empty request");
        }

        // create the profile
        UserProfile profile = composeUserProfileEntityFromRequest(request, user);

        // save the profile
        try {
            profile = userProfileRepository.save(profile);
        } catch (Exception ex) {
            log.error("Error creating new user profile: {} - {}", ex.getClass(), ex.getMessage());
            throw new Exception("Error creating new user profile", ex);
        }

        // set profile on user
        user.setUserProfile(profile);

        return Optional.of(profile);
    }

    private Optional<UserAddress> createAndSaveUserAddressFromRequest(UserAddressRequestDto request, User user)
        throws Exception {
        if (request == null || user == null) {
            log.warn("Cannot create user address from null request or user");
            throw new InvalidRequestDataException("Cannot create user address from null or empty request");
        }

        UserAddress address = composeUserAddressEntityFromRequest(request, user);

        try {
            address = userAddressRepository.saveAndFlush(address);
        } catch (Exception ex) {
            log.error("Error creating new user address for user {}: {}", user.getId(), ex.getMessage());
            throw new Exception("Error creating new user address", ex);
        }

        // set address on user
        user.addAddress(address);

        return Optional.of(address);
    }

    private User composeUserEntityFromRequest(CreateUserRequestDto request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPassword()); // TODO: hash this..
        user.setStatus("active");  // TODO: default to active, need to make this an enum
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return user;
    }

    private UserProfile composeUserProfileEntityFromRequest(CreateUserRequestDto request, User user) {
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setProfileImageUrl(request.getProfileImageUrl());
        profile.setCreatedAt(Instant.now());
        profile.setUpdatedAt(Instant.now());
        return profile;
    }

    private UserAddress composeUserAddressEntityFromRequest(UserAddressRequestDto request, User user) {
        UserAddress address = new UserAddress();
        address.setUser(user);
        address.setAddressType(request.getAddressType());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        address.setCountry(request.getCountry());
        address.setCreatedAt(Instant.now());
        address.setUpdatedAt(Instant.now());
        return address;
    }

}
