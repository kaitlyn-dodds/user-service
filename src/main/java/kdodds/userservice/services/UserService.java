package kdodds.userservice.services;

import kdodds.userservice.dto.requests.CreateUserRequestDto;
import kdodds.userservice.dto.requests.PatchUserRequestDto;
import kdodds.userservice.dto.responses.PageDto;
import kdodds.userservice.dto.responses.PagedUsersResponseDto;
import kdodds.userservice.dto.responses.UserProfileResponseDto;
import kdodds.userservice.dto.responses.UserResponseDto;
import kdodds.userservice.entities.User;
import kdodds.userservice.entities.UserProfile;
import kdodds.userservice.exceptions.models.exceptions.InvalidRequestDataException;
import kdodds.userservice.exceptions.models.exceptions.InvalidUserIdException;
import kdodds.userservice.exceptions.models.exceptions.UserConflictException;
import kdodds.userservice.exceptions.models.exceptions.UserNotFoundException;
import kdodds.userservice.exceptions.models.exceptions.UserProfileNotFound;
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
     * Creates a new user. Returns the newly created user as a UserResponseDto.
     *
     * @param request The CreateUserRequestDto to use for the data.
     * @return UserResponseDto
     * @throws Exception Throws an exception if the request is invalid or attempt to create user fails.
     */
    public UserResponseDto createUserAndProfileAndAddress(CreateUserRequestDto request) throws Exception {
        if (request == null) {
            log.warn("Cannot create user from null request");
            throw new InvalidRequestDataException("Cannot create user from null or empty request.");
        }

        if (request.getAddress() == null) {
            return createUserAndProfile(request);
        }

        try {
            UUID userId = userRepository.createUserAndProfileAndAddress(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                request.getPhoneNumber(),
                request.getProfileImageUrl(),
                request.getAddress().getAddressType(),
                request.getAddress().getAddressLine1(),
                request.getAddress().getAddressLine2(),
                request.getAddress().getCity(),
                request.getAddress().getState(),
                request.getAddress().getZipCode(),
                request.getAddress().getCountry()
            );

            return UserResponseDto.fromEntity(userRepository.findById(userId).get());
        } catch (DataIntegrityViolationException ex) {
            String msg = handleDataIntegrityViolationException(ex, request);
            log.error(msg);
            throw new Exception(msg, ex);
        } catch (Exception ex) {
            log.error("Error creating new user: {} - {}", request.getUsername(), ex.getMessage());
            throw new Exception("Error creating new user", ex);
        }
    }

    /**
     * Creates a new user and profile. Returns the newly created user as a UserResponseDto.
     *
     * @param request The CreateUserRequestDto to use for the data.
     * @return UserResponseDto
     * @throws Exception Throws an exception if the request is invalid or attempt to create user fails.
     */
    public UserResponseDto createUserAndProfile(CreateUserRequestDto request) throws Exception {
        if (request == null) {
            log.warn("Cannot create user from null request");
            throw new InvalidRequestDataException("Cannot create user from null or empty request");
        }

        try {
            UUID userId = userRepository.createUserAndProfile(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                request.getPhoneNumber(),
                request.getProfileImageUrl()
            );

            return UserResponseDto.fromEntity(userRepository.findById(userId).get());
        } catch (DataIntegrityViolationException ex) {
            String msg = handleDataIntegrityViolationException(ex, request);
            log.error(msg);
            throw new Exception(msg, ex);
        } catch (Exception ex) {
            log.error("Error creating new user: {} - {}", request.getUsername(), ex.getMessage());
            throw new Exception("Error creating new user", ex);
        }
    }

    /**
     * Deletes a user by user id.
     *
     * @param userId The user id of the user to delete.
     * @throws Exception Throws an exception if the user cannot be deleted.
     */
    public void deleteUserByUserId(String userId) throws Exception {
        if (userId == null || userId.isEmpty()) {
            log.error("Cannot delete user with null or empty userId.");
            throw new InvalidUserIdException();
        }

        try {
            userRepository.deleteById(UUID.fromString(userId));
        } catch (Exception ex) {
            log.error("Error deleting user with id: {}", userId, ex);
            throw new Exception(
                String.format("Delete user by id for user id %s failed for unknown reasons", userId),
                ex
            );
        }
    }

    /**
     * Updates a user by user id.
     *
     * @param userId The user id of the user to update.
     * @param request The PatchUserRequestDto to use for the data.
     * @return UserResponseDto
     * @throws Exception Throws an exception if the request is invalid or attempt to update the user fails.
     */
    public UserResponseDto updateUser(String userId, PatchUserRequestDto request) throws Exception {
        if (userId == null || userId.isEmpty()) {
            log.error("Cannot update user with null or empty userId.");
            throw new InvalidUserIdException();
        }

        // TODO: allow complex changes, for now, do not allow changes to username or email
        if ((request.getEmail() != null && !request.getEmail().isEmpty())
            || request.getUsername() != null && !request.getUsername().isEmpty()) {
            throw new InvalidRequestDataException("Cannot change username or email");
        }

        // build the updated entity
        User user = userRepository.findById(UUID.fromString(userId))
            .orElseThrow(() -> new UserNotFoundException(userId));

        boolean updateNeeded = applyUpdates(request, user);

        if (!updateNeeded) {
            log.info("No changes detected for update user with id: {}", userId);
            return UserResponseDto.fromEntity(user);
        }

        // set the updated at timestamp
        user.setUpdatedAt(Instant.now());

        try {
            user = userRepository.save(user);
            return UserResponseDto.fromEntity(user);
        } catch (Exception ex) {
            log.error("Error updating user with id: {}", userId, ex);
            throw new Exception(
                String.format("Update user by id for user id %s failed for unknown reasons", userId),
                ex
            );
        }
    }

    private static boolean applyUpdates(PatchUserRequestDto request, User user) {
        // track if write to db is really necessary
        boolean updateNeeded = false;

        // first name cannot be set to null or empty, also check if different
        if (request.getFirstName() != null && !request.getFirstName().equals(user.getUserProfile().getFirstName())) {
            // if the first name is empty, throw InvalidRequestDataException
            if (request.getFirstName().isEmpty()) {
                throw new InvalidRequestDataException("First name cannot be empty");
            }

            user.getUserProfile().setFirstName(request.getFirstName());
            updateNeeded = true;
        }

        // last name cannot be set to null or empty, also check if different
        if (request.getLastName() != null && !request.getLastName().equals(user.getUserProfile().getLastName())) {
            // if the last name is empty, throw InvalidRequestDataException
            if (request.getLastName().isEmpty()) {
                throw new InvalidRequestDataException("Last name cannot be empty");
            }

            user.getUserProfile().setLastName(request.getLastName());
            updateNeeded = true;
        }

        // phone number cannot be set to null or empty, also check if different
        if (request.getPhoneNumber() != null
            && !request.getPhoneNumber().equals(user.getUserProfile().getPhoneNumber())) {
            // if the phone number is empty, throw InvalidRequestDataException
            if (request.getPhoneNumber().isEmpty()) {
                throw new InvalidRequestDataException("Phone number cannot be empty");
            }

            user.getUserProfile().setPhoneNumber(request.getPhoneNumber());
            updateNeeded = true;
        }

        // user profile image cannot be null but can be empty, also check if different
        if (request.getProfileImageUrl() != null
            && !request.getProfileImageUrl().equals(user.getUserProfile().getProfileImageUrl())) {
            // if the image url is empty, set it to null in the db
            if (request.getProfileImageUrl().isEmpty()) {
                user.getUserProfile().setProfileImageUrl(null);
            } else {
                user.getUserProfile().setProfileImageUrl(request.getProfileImageUrl());
            }

            updateNeeded = true;
        }

        return updateNeeded;
    }

    private String handleDataIntegrityViolationException(
        DataIntegrityViolationException ex,
        CreateUserRequestDto request
    ) {
        String exceptionMessage = String.format(
            "Unknown error creating user with username: %s, email %s",
            request.getUsername(),
            request.getEmail()
        );

        // check if due to uniqueness violation
        if (ex.getCause() instanceof  org.hibernate.exception.ConstraintViolationException cve) {
            if (cve.getMessage().contains("users_username_key")) {
                exceptionMessage = String.format("User with username %s already exists", request.getUsername());
                throw new UserConflictException(exceptionMessage);
            }
            if (cve.getMessage().contains("email")) {
                exceptionMessage = String.format("User with email %s already exists", request.getEmail());
            }
            throw new UserConflictException(exceptionMessage);
        }

        return exceptionMessage;
    }

}
