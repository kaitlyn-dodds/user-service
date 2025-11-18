package kdodds.userservice.services;

import kdodds.userservice.dto.requests.CreateUserRequestDto;
import kdodds.userservice.dto.requests.PatchUserRequestDto;
import kdodds.userservice.dto.responses.UserProfileResponseDto;
import kdodds.userservice.dto.responses.UserResponseDto;
import kdodds.userservice.entities.User;
import kdodds.userservice.exceptions.models.exceptions.InvalidRequestDataException;
import kdodds.userservice.exceptions.models.exceptions.InvalidUserIdException;
import kdodds.userservice.exceptions.models.exceptions.UserConflictException;
import kdodds.userservice.exceptions.models.exceptions.UserNotFoundException;
import kdodds.userservice.exceptions.models.exceptions.UserProfileNotFound;
import kdodds.userservice.repositories.UserAddressRepository;
import kdodds.userservice.repositories.UserProfileRepository;
import kdodds.userservice.repositories.UserRepository;
import kdodds.userservice.utils.TestDataFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private UserProfileRepository mockUserProfileRepository;

    @Mock
    private UserAddressRepository mockUserAddressRepository;

    @InjectMocks
    private UserService userService;

    /**
     * Setup for each test.
     */
    @BeforeEach
    public void setup() {
        // clear mocks
        Mockito.reset(
            mockUserRepository,
            mockUserProfileRepository,
            mockUserAddressRepository
        );
    }

    /**
     * Tests getUserResponse returns a valid UserResponseDto when a user exists.
     */
    @Test
    public void testGetUserResponseDto_UserExists() throws Exception {
        String userId = TestDataFactory.TEST_USER_ID;

        // mock user repository response
        Mockito.when(mockUserRepository.findById(UUID.fromString(userId))).thenReturn(
            Optional.of(TestDataFactory.createTestUserEntity(userId, true))
        );

        UserResponseDto response = userService.getUserResponseDto(userId);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(userId, response.getUserId());
        Assertions.assertEquals(TestDataFactory.TEST_USER_USERNAME, response.getUsername());
        Assertions.assertEquals(TestDataFactory.TEST_USER_EMAIL, response.getEmail());
        Assertions.assertEquals(TestDataFactory.TEST_USER_FIRST_NAME, response.getFirstName());
        Assertions.assertEquals(TestDataFactory.TEST_USER_LAST_NAME, response.getLastName());
        Assertions.assertEquals(TestDataFactory.TEST_USER_PHONE_NUMBER, response.getPhoneNumber());
        Assertions.assertEquals(TestDataFactory.TEST_USER_PROFILE_IMAGE_URL, response.getProfileImageUrl());
        Assertions.assertNotNull(response.getCreatedAt());
        Assertions.assertNotNull(response.getUpdatedAt());

        // validate addresses
        Assertions.assertNotNull(response.getAddresses());
        Assertions.assertEquals(1, response.getAddresses().size());
        Assertions.assertEquals(
            TestDataFactory.TEST_USER_ADDRESS_LINE_1,
            response.getAddresses().getFirst().getAddressLine1()
        );
        Assertions.assertEquals(
            TestDataFactory.TEST_USER_ADDRESS_TYPE,
            response.getAddresses().getFirst().getAddressType()
        );
        Assertions.assertEquals(TestDataFactory.TEST_USER_CITY, response.getAddresses().getFirst().getCity());
        Assertions.assertEquals(TestDataFactory.TEST_USER_STATE, response.getAddresses().getFirst().getState());
        Assertions.assertEquals(
            TestDataFactory.TEST_USER_ZIP_CODE,
            response.getAddresses().getFirst().getZipCode()
        );
        Assertions.assertEquals(TestDataFactory.TEST_USER_COUNTRY, response.getAddresses().getFirst().getCountry());
        Assertions.assertNotNull(response.getAddresses().getFirst().getCreatedAt());
        Assertions.assertNotNull(response.getAddresses().getFirst().getUpdatedAt());

    }

    /**
     * Test getUserResponse throws an InvalidUserIdException when the user id is empty.
     */
    @Test
    public void testGetUserResponseDto_MissingId_ThrowsInvalidUserIdException() {
        String userId = "";

        try {
            userService.getUserResponseDto(userId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test getUserResponse throws an InvalidUserIdException when the user id is null.
     */
    @Test
    public void testGetUserResponseDto_NullId_ThrowsInvalidUserIdException() {
        String userId = null;

        try {
            userService.getUserResponseDto(userId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test getUserResponse throws an exception when the call to the user repository fails.
     */
    @Test
    public void testGetUserResponseDto_RepositoryCallFails_ThrowsException() {
        String userId = TestDataFactory.TEST_USER_ID;

        // mock user repository response
        Mockito.when(mockUserRepository.findById(UUID.fromString(userId))).thenThrow(
            new RuntimeException("mock exception")
        );

        try {
            userService.getUserResponseDto(userId);
            Assertions.fail("Expected exception not thrown");
        } catch (Exception ex) {
            Assertions.assertEquals(
                String.format("Find user by id for user id %s failed for unknown reasons", userId),
                ex.getMessage()
            );
        }
    }

    /**
     * Test getUserResponse throws a UserNotFoundException when the user does not exist.
     */
    @Test
    public void testGetUserResponseDto_UserDoesNotExist_ThrowsUserNotFoundException() {
        String userId = TestDataFactory.TEST_USER_ID;

        // mock user repository response
        Mockito.when(mockUserRepository.findById(UUID.fromString(userId))).thenReturn(
            Optional.empty()
        );

        try {
            userService.getUserResponseDto(userId);
            Assertions.fail("Expected exception not thrown");
        } catch (UserNotFoundException ex) {
            Assertions.assertEquals(
                String.format("User with id %s not found", userId),
                ex.getMessage()
            );
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test getUserProfileDtoByUserId returns a valid UserProfileResponseDto when a user exists.
     */
    @Test
    public void testGetUserProfileDtoByUserId_UserExists() throws Exception {
        String userId = TestDataFactory.TEST_USER_ID;

        // mock user profile repository response
        Mockito.when(mockUserProfileRepository.findById(UUID.fromString(userId))).thenReturn(
            Optional.of(TestDataFactory.createTestUserProfileEntity(userId))
        );

        UserProfileResponseDto response = userService.getUserProfileDtoByUserId(userId);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(userId, response.getUserId());
        Assertions.assertEquals(TestDataFactory.TEST_USER_FIRST_NAME, response.getFirstName());
        Assertions.assertEquals(TestDataFactory.TEST_USER_LAST_NAME, response.getLastName());
        Assertions.assertEquals(TestDataFactory.TEST_USER_PHONE_NUMBER, response.getPhoneNumber());
        Assertions.assertEquals(TestDataFactory.TEST_USER_PROFILE_IMAGE_URL, response.getProfileImageUrl());
        Assertions.assertNotNull(response.getCreatedAt());
        Assertions.assertNotNull(response.getUpdatedAt());
    }

    /**
     * Test getUserProfileDtoByUserId throws an InvalidUserIdException when the user id is empty.
     */
    @Test
    public void testGetUserProfileDtoByUserId_MissingId_ThrowsInvalidUserIdException() {
        String userId = "";

        try {
            userService.getUserProfileDtoByUserId(userId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test getUserProfileDtoByUserId throws an InvalidUserIdException when the user id is null.
     */
    @Test
    public void testGetUserProfileDtoByUserId_NullId_ThrowsInvalidUserIdException() {
        String userId = null;

        try {
            userService.getUserProfileDtoByUserId(userId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test getUserProfileDtoByUserId throws an exception when the call to the user profile repository fails.
     */
    @Test
    public void testGetUserProfileDtoByUserId_RepositoryCallFails_ThrowsException() {
        String userId = TestDataFactory.TEST_USER_ID;

        // mock user profile repository response
        Mockito.when(mockUserProfileRepository.findById(UUID.fromString(userId))).thenThrow(
            new RuntimeException("mock exception")
        );

        try {
            userService.getUserProfileDtoByUserId(userId);
            Assertions.fail("Expected exception not thrown");
        } catch (Exception ex) {
            Assertions.assertEquals(
                String.format("Find user profile by id for userId %s failed for unknown reasons", userId),
                ex.getMessage()
            );
        }
    }

    /**
     * Test getUserProfileDtoByUserId throws a UserNotFoundException when the user does not exist.
     */
    @Test
    public void testGetUserProfileDtoByUserId_UserProfileDoesNotExist_ThrowsUserProfileNotFoundException() {
        String userId = TestDataFactory.TEST_USER_ID;

        // mock user profile repository response
        Mockito.when(mockUserProfileRepository.findById(UUID.fromString(userId))).thenReturn(
            Optional.empty()
        );

        try {
            userService.getUserProfileDtoByUserId(userId);
            Assertions.fail("Expected exception not thrown");
        } catch (UserProfileNotFound ex) {
            Assertions.assertEquals(
                String.format("User profile not found for user id: %s", userId),
                ex.getMessage()
            );
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the createUserAndProfileAndAndAddress method creates a valid user, profile, and address when given a valid
     * CreateUserRequestDto.
     */
    @Test
    public void testCreateUserAndProfileAndAndAddress_ValidRequest_CreatesAllEntities() {
        CreateUserRequestDto request = TestDataFactory.createUserRequestDto();

        // mock the response from the repository to create the user, profile, and address
        Mockito.when(mockUserRepository.createUserAndProfileAndAddress(
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
        )).thenReturn(UUID.fromString(TestDataFactory.TEST_USER_ID));

        // mock the response from the repository to find the created user
        Mockito.when(mockUserRepository.findById(UUID.fromString(TestDataFactory.TEST_USER_ID))).thenReturn(
            Optional.of(TestDataFactory.createTestUserEntity(TestDataFactory.TEST_USER_ID, true))
        );

        try {
            UserResponseDto response = userService.createUserAndProfileAndAddress(request);

            // validate response
            Assertions.assertNotNull(response);
            Assertions.assertEquals(TestDataFactory.TEST_USER_ID, response.getUserId());
            Assertions.assertEquals(TestDataFactory.TEST_USER_USERNAME, response.getUsername());
            Assertions.assertEquals(TestDataFactory.TEST_USER_EMAIL, response.getEmail());
            Assertions.assertEquals(TestDataFactory.TEST_USER_FIRST_NAME, response.getFirstName());
            Assertions.assertEquals(TestDataFactory.TEST_USER_LAST_NAME, response.getLastName());
            Assertions.assertEquals(TestDataFactory.TEST_USER_PHONE_NUMBER, response.getPhoneNumber());
            Assertions.assertEquals(TestDataFactory.TEST_USER_PROFILE_IMAGE_URL, response.getProfileImageUrl());
            Assertions.assertNotNull(response.getAddresses());
            Assertions.assertEquals(1, response.getAddresses().size());
            Assertions.assertNotNull(response.getCreatedAt());
            Assertions.assertNotNull(response.getUpdatedAt());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the createUserAndProfileAndAddress method creates a user and profile when given a valid request without an
     * address.
     */
    @Test
    public void testCreateUserAndProfileAndAndAddress_ValidRequestWithoutAddress_CreatesUserAndProfile() {
        CreateUserRequestDto request = TestDataFactory.createUserRequestDto();
        request.setAddress(null);

        // mock the response from the repository to create the user and profile
        Mockito.when(mockUserRepository.createUserAndProfile(
            request.getUsername(),
            request.getEmail(),
            request.getPassword(),
            request.getFirstName(),
            request.getLastName(),
            request.getPhoneNumber(),
            request.getProfileImageUrl()
        )).thenReturn(UUID.fromString(TestDataFactory.TEST_USER_ID));

        // mock the response from the repository to find the created user
        Mockito.when(mockUserRepository.findById(UUID.fromString(TestDataFactory.TEST_USER_ID))).thenReturn(
            Optional.of(TestDataFactory.createTestUserEntity(TestDataFactory.TEST_USER_ID, false))
        );

        try {
            UserResponseDto response = userService.createUserAndProfileAndAddress(request);

            // validate response
            Assertions.assertNotNull(response);
            Assertions.assertEquals(TestDataFactory.TEST_USER_ID, response.getUserId());
            Assertions.assertEquals(TestDataFactory.TEST_USER_USERNAME, response.getUsername());
            Assertions.assertEquals(TestDataFactory.TEST_USER_EMAIL, response.getEmail());
            Assertions.assertEquals(TestDataFactory.TEST_USER_FIRST_NAME, response.getFirstName());
            Assertions.assertEquals(TestDataFactory.TEST_USER_LAST_NAME, response.getLastName());
            Assertions.assertEquals(TestDataFactory.TEST_USER_PHONE_NUMBER, response.getPhoneNumber());
            Assertions.assertEquals(TestDataFactory.TEST_USER_PROFILE_IMAGE_URL, response.getProfileImageUrl());
            Assertions.assertNotNull(response.getAddresses());
            Assertions.assertEquals(0, response.getAddresses().size());
            Assertions.assertNotNull(response.getCreatedAt());
            Assertions.assertNotNull(response.getUpdatedAt());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the createUserAndProfileAndAddress method throws an exception when the request is null.
     */
    @Test
    public void testCreateUserAndProfileAndAndAddress_NullRequest_ThrowsInvalidRequestDataException() {
        CreateUserRequestDto request = null;

        try {
            userService.createUserAndProfileAndAddress(request);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Cannot create user from null or empty request.", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the createUserAndProfileAndAddress method throws a UserConflictException when the username already exists.
     */
    @Test
    public void testCreateUserAndProfileAndAndAddress_UsernameAlreadyExists_ThrowsUserConflictException() {
        CreateUserRequestDto request = TestDataFactory.createUserRequestDto();

        // mock the response from the repository to throw a DataIntegrityViolationException with a
        // ConstraintViolationException cause
        Mockito.when(mockUserRepository.createUserAndProfileAndAddress(
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
        )).thenThrow(
            new DataIntegrityViolationException(
                "Key constraint",
                new org.hibernate.exception.ConstraintViolationException(
                    "users_username_key must be unique",
                    new SQLException(),
                    ""
                )
            )
        );

        String exceptionMessage = String.format("User with username %s already exists", request.getUsername());
        try {
            userService.createUserAndProfileAndAddress(request);
            Assertions.fail("Expected UserConflictException not thrown");
        } catch (UserConflictException ex) {
            Assertions.assertEquals(exceptionMessage, ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the createUserAndProfileAndAddress method throws a UserConflictException when the email already exists.
     */
    @Test
    public void testCreateUserAndProfileAndAndAddress_EmailAlreadyExists_ThrowsUserConflictException() {
        CreateUserRequestDto request = TestDataFactory.createUserRequestDto();

        // mock the response from the repository to throw a DataIntegrityViolationException with a
        // ConstraintViolationException cause
        Mockito.when(mockUserRepository.createUserAndProfileAndAddress(
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
        )).thenThrow(
            new DataIntegrityViolationException(
                "Key constraint",
                new org.hibernate.exception.ConstraintViolationException(
                    "email must be unique",
                    new SQLException(),
                    ""
                )
            )
        );

        String exceptionMessage = String.format("User with email %s already exists", request.getEmail());
        try {
            userService.createUserAndProfileAndAddress(request);
            Assertions.fail("Expected UserConflictException not thrown");
        } catch (UserConflictException ex) {
            Assertions.assertEquals(exceptionMessage, ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the UserController createUserAndProfile method throws a UserConflictException when the username already
     * exists.
     */
    @Test
    public void testCreateUserAndProfile_UsernameAlreadyExists_ThrowsUserConflictException() {
        CreateUserRequestDto request = TestDataFactory.createUserRequestDto();
        request.setAddress(null);

        // mock the response from the repository to throw a DataIntegrityViolationException with a
        // ConstraintViolationException cause
        Mockito.when(mockUserRepository.createUserAndProfile(
            request.getUsername(),
            request.getEmail(),
            request.getPassword(),
            request.getFirstName(),
            request.getLastName(),
            request.getPhoneNumber(),
            request.getProfileImageUrl()
        )).thenThrow(
            new DataIntegrityViolationException(
                "Key constraint",
                new org.hibernate.exception.ConstraintViolationException(
                    "users_username_key must be unique",
                    new SQLException(),
                    ""
                )
            )
        );

        String exceptionMessage = String.format("User with username %s already exists", request.getUsername());
        try {
            userService.createUserAndProfileAndAddress(request);
            Assertions.fail("Expected UserConflictException not thrown");
        } catch (UserConflictException ex) {
            Assertions.assertEquals(exceptionMessage, ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the UserController createUserAndProfile method throws a UserConflictException when the email already exists.
     */
    @Test
    public void testCreateUserAndProfile_EmailAlreadyExists_ThrowsUserConflictException() {
        CreateUserRequestDto request = TestDataFactory.createUserRequestDto();
        request.setAddress(null);

        // mock the response from the repository to throw a DataIntegrityViolationException with a
        // ConstraintViolationException cause
        Mockito.when(mockUserRepository.createUserAndProfile(
            request.getUsername(),
            request.getEmail(),
            request.getPassword(),
            request.getFirstName(),
            request.getLastName(),
            request.getPhoneNumber(),
            request.getProfileImageUrl()
        )).thenThrow(
            new DataIntegrityViolationException(
                "Key constraint",
                new org.hibernate.exception.ConstraintViolationException(
                    "email must be unique",
                    new SQLException(),
                    ""
                )
            )
        );

        String exceptionMessage = String.format("User with email %s already exists", request.getEmail());
        try {
            userService.createUserAndProfileAndAddress(request);
            Assertions.fail("Expected UserConflictException not thrown");
        } catch (UserConflictException ex) {
            Assertions.assertEquals(exceptionMessage, ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the createUserAndProfileAndAddress method throws an Exception when a generic error occurs during creation.
     */
    @Test
    public void testCreateUserAndProfileAndAndAddress_GenericError_ThrowsException() {
        CreateUserRequestDto request = TestDataFactory.createUserRequestDto();

        // mock the response from the repository to throw a generic exception
        Mockito.when(mockUserRepository.createUserAndProfileAndAddress(
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
        )).thenThrow(
            new RuntimeException("mock exception")
        );

        try {
            userService.createUserAndProfileAndAddress(request);
            Assertions.fail("Expected exception not thrown");
        } catch (Exception ex) {
            Assertions.assertEquals("Error creating new user", ex.getMessage());
        }
    }

    /**
     * Test the createUserAndProfile method throws an Exception when a generic error occurs during creation.
     */
    @Test
    public void testCreateUserAndProfile_GenericError_ThrowsException() {
        CreateUserRequestDto request = TestDataFactory.createUserRequestDto();
        request.setAddress(null);

        // mock the response from the repository to throw a generic exception
        Mockito.when(mockUserRepository.createUserAndProfile(
            request.getUsername(),
            request.getEmail(),
            request.getPassword(),
            request.getFirstName(),
            request.getLastName(),
            request.getPhoneNumber(),
            request.getProfileImageUrl()
        )).thenThrow(
            new RuntimeException("mock exception")
        );

        try {
            userService.createUserAndProfileAndAddress(request);
            Assertions.fail("Expected exception not thrown");
        } catch (Exception ex) {
            Assertions.assertEquals("Error creating new user", ex.getMessage());
        }
    }

    /**
     * Test the deleteUserByUserId method deletes a user when given a valid user id.
     */
    @Test
    public void testDeleteUserByUserId_ValidUserId_DeletesUser() {
        String userId = TestDataFactory.TEST_USER_ID;

        // mock the response from the repository to delete the user
        Mockito.doNothing().when(mockUserRepository).deleteById(UUID.fromString(userId));

        try {
            userService.deleteUserByUserId(userId);
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }

        // validate the repository call was made
        Mockito.verify(mockUserRepository, Mockito.times(1)).deleteById(UUID.fromString(userId));
    }

    /**
     * Test the deleteUserByUserId method throws an exception when the user id is null.
     */
    @Test
    public void testDeleteUserByUserId_NullUserId_ThrowsException() {
        String userId = null;

        try {
            userService.deleteUserByUserId(userId);
            Assertions.fail("Expected exception not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }

        // validate the repository call was not made
        Mockito.verify(mockUserRepository, Mockito.never()).deleteById(any());
    }

    /**
     * Test the deleteUserByUserId method throws an exception when the user id is empty.
     */
    @Test
    public void testDeleteUserByUserId_EmptyUserId_ThrowsException() {
        String userId = "";

        try {
            userService.deleteUserByUserId(userId);
            Assertions.fail("Expected exception not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }

        // validate the repository call was not made
        Mockito.verify(mockUserRepository, Mockito.never()).deleteById(any());
    }

    /**
     * Test the deleteUserByUserId method throws an exception when the repository call fails.
     */
    @Test
    public void testDeleteUserByUserId_RepositoryCallFails_ThrowsException() {
        String userId = TestDataFactory.TEST_USER_ID;

        // mock the repository to throw an exception
        Mockito.doThrow(new RuntimeException("mock exception"))
            .when(mockUserRepository).deleteById(UUID.fromString(userId));

        try {
            userService.deleteUserByUserId(userId);
            Assertions.fail("Expected exception not thrown");
        } catch (Exception ex) {
            Assertions.assertEquals(
                String.format("Delete user by id for user id %s failed for unknown reasons", userId),
                ex.getMessage()
            );
        }

        // validate the repository call was made
        Mockito.verify(mockUserRepository, Mockito.times(1)).deleteById(UUID.fromString(userId));
    }

    /**
     * Test the updateUser method updates a user when given a valid request with all fields populated.
     */
    @Test
    public void testUpdateUser_ValidCompleteRequest_UpdatesUser() {
        String userId = TestDataFactory.TEST_USER_ID;
        PatchUserRequestDto request = TestDataFactory.createPatchUserRequestDto();
        User mockUser = TestDataFactory.createTestUserEntity(userId, false);
        Instant originalUpdatedAt = mockUser.getUpdatedAt();

        // mock the user repository response
        Mockito.when(mockUserRepository.findById(UUID.fromString(userId))).thenReturn(
            Optional.of(mockUser)
        );

        // mock the response from the repository to update the user
        Mockito.when(mockUserRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        try {
            UserResponseDto response = userService.updateUser(userId, request);

            // validate response
            Assertions.assertNotNull(response);
            Assertions.assertEquals(userId, response.getUserId());
            Assertions.assertEquals(TestDataFactory.TEST_USER_USERNAME, response.getUsername());
            Assertions.assertEquals(TestDataFactory.TEST_USER_EMAIL, response.getEmail());
            Assertions.assertEquals(mockUser.getCreatedAt(), response.getCreatedAt());

            // validate changed values
            Assertions.assertEquals(request.getFirstName(), response.getFirstName());
            Assertions.assertEquals(request.getLastName(), response.getLastName());
            Assertions.assertEquals(request.getPhoneNumber(), response.getPhoneNumber());
            Assertions.assertEquals(request.getProfileImageUrl(), response.getProfileImageUrl());

            // updated at should be changed
            Assertions.assertTrue(response.getUpdatedAt().isAfter(originalUpdatedAt));

        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }

        // validate the repository calls were made
        Mockito.verify(mockUserRepository, Mockito.times(1)).findById(UUID.fromString(userId));
        Mockito.verify(mockUserRepository, Mockito.times(1)).save(any());
    }

    /**
     * Test the updateUser method updates a user when given a valid request with only the first name and last name
     * changed in the request.
     */
    @Test
    public void testUpdateUser_ValidPartialRequest_UpdatesUser() {
        String userId = TestDataFactory.TEST_USER_ID;
        PatchUserRequestDto request = TestDataFactory.createPatchUserRequestDto();
        User mockUser = TestDataFactory.createTestUserEntity(userId, false);
        Instant originalUpdatedAt = mockUser.getUpdatedAt();

        // set the phone number, profile image url to null in the request
        request.setPhoneNumber(null);
        request.setProfileImageUrl(null);

        // mock the user repository response
        Mockito.when(mockUserRepository.findById(UUID.fromString(userId))).thenReturn(
            Optional.of(mockUser)
        );

        // mock the response from the repository to update the user
        Mockito.when(mockUserRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        try {
            UserResponseDto response = userService.updateUser(userId, request);

            // validate response
            Assertions.assertNotNull(response);
            Assertions.assertEquals(userId, response.getUserId());
            Assertions.assertEquals(TestDataFactory.TEST_USER_USERNAME, response.getUsername());
            Assertions.assertEquals(TestDataFactory.TEST_USER_EMAIL, response.getEmail());
            Assertions.assertEquals(mockUser.getCreatedAt(), response.getCreatedAt());

            // validate changed values
            Assertions.assertEquals(request.getFirstName(), response.getFirstName());
            Assertions.assertEquals(request.getLastName(), response.getLastName());

            // validate phone number, profile image url not changed
            Assertions.assertEquals(mockUser.getUserProfile().getPhoneNumber(), response.getPhoneNumber());
            Assertions.assertEquals(mockUser.getUserProfile().getProfileImageUrl(), response.getProfileImageUrl());

            // validate updated at changed
            Assertions.assertTrue(response.getUpdatedAt().isAfter(originalUpdatedAt));
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }

        // validate the repository calls were made
        Mockito.verify(mockUserRepository, Mockito.times(1)).findById(UUID.fromString(userId));
        Mockito.verify(mockUserRepository, Mockito.times(1)).save(any());
    }

    /**
     * Test that the updateUser method updates a user when given a valid request with only the phone number changed in
     * the request.
     */
    @Test
    public void testUpdateUser_ValidPhoneNumberRequest_UpdatesUser() {
        String userId = TestDataFactory.TEST_USER_ID;
        PatchUserRequestDto request = TestDataFactory.createPatchUserRequestDto();
        User mockUser = TestDataFactory.createTestUserEntity(userId, false);
        Instant originalUpdatedAt = mockUser.getUpdatedAt();

        // set the first name, last name, and profile image url to null in the request
        request.setFirstName(null);
        request.setLastName(null);
        request.setProfileImageUrl(null);

        // mock the user repository response
        Mockito.when(mockUserRepository.findById(UUID.fromString(userId))).thenReturn(
            Optional.of(mockUser)
        );

        // mock the response from the repository to update the user
        Mockito.when(mockUserRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        try {
            UserResponseDto response = userService.updateUser(userId, request);

            // validate response
            Assertions.assertNotNull(response);
            Assertions.assertEquals(userId, response.getUserId());
            Assertions.assertEquals(TestDataFactory.TEST_USER_USERNAME, response.getUsername());
            Assertions.assertEquals(TestDataFactory.TEST_USER_EMAIL, response.getEmail());
            Assertions.assertEquals(mockUser.getCreatedAt(), response.getCreatedAt());

            // validate changed values
            Assertions.assertEquals(request.getPhoneNumber(), response.getPhoneNumber());

            // validate first name, last name, and profile image url not changed
            Assertions.assertEquals(mockUser.getUserProfile().getFirstName(), response.getFirstName());
            Assertions.assertEquals(mockUser.getUserProfile().getLastName(), response.getLastName());
            Assertions.assertEquals(mockUser.getUserProfile().getProfileImageUrl(), response.getProfileImageUrl());

            // validate updated at changed
            Assertions.assertTrue(response.getUpdatedAt().isAfter(originalUpdatedAt));
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }

        // validate the repository calls were made
        Mockito.verify(mockUserRepository, Mockito.times(1)).findById(UUID.fromString(userId));
        Mockito.verify(mockUserRepository, Mockito.times(1)).save(any());
    }

    /**
     * Test the updateUser method updates a user when given a valid request with only the profile image changed in the
     * request.
     */
    @Test
    public void testUpdateUser_ValidProfileImageRequest_UpdatesUser() {
        String userId = TestDataFactory.TEST_USER_ID;
        PatchUserRequestDto request = TestDataFactory.createPatchUserRequestDto();
        User mockUser = TestDataFactory.createTestUserEntity(userId, false);
        Instant originalUpdatedAt = mockUser.getUpdatedAt();

        // set the first name, last name, and phone number to null in the request
        request.setFirstName(null);
        request.setLastName(null);
        request.setPhoneNumber(null);

        // mock the user repository response
        Mockito.when(mockUserRepository.findById(UUID.fromString(userId))).thenReturn(
            Optional.of(mockUser)
        );

        // mock the response from the repository to update the user
        Mockito.when(mockUserRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        try {
            UserResponseDto response = userService.updateUser(userId, request);

            // validate response
            Assertions.assertNotNull(response);
            Assertions.assertEquals(userId, response.getUserId());
            Assertions.assertEquals(TestDataFactory.TEST_USER_USERNAME, response.getUsername());
            Assertions.assertEquals(TestDataFactory.TEST_USER_EMAIL, response.getEmail());
            Assertions.assertEquals(mockUser.getCreatedAt(), response.getCreatedAt());

            // validate changed values
            Assertions.assertEquals(request.getProfileImageUrl(), response.getProfileImageUrl());

            // validate first name, last name, and phone number not changed
            Assertions.assertEquals(mockUser.getUserProfile().getFirstName(), response.getFirstName());
            Assertions.assertEquals(mockUser.getUserProfile().getLastName(), response.getLastName());
            Assertions.assertEquals(mockUser.getUserProfile().getPhoneNumber(), response.getPhoneNumber());

            // validate updated at changed
            Assertions.assertTrue(response.getUpdatedAt().isAfter(originalUpdatedAt));
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }

        // validate the repository calls were made
        Mockito.verify(mockUserRepository, Mockito.times(1)).findById(UUID.fromString(userId));
        Mockito.verify(mockUserRepository, Mockito.times(1)).save(any());
    }

    /**
     * Test the updateUser method allows the profile image url property to be set to an empty string.
     */
    @Test
    public void testUpdateUser_ValidEmptyProfileImageUrlRequest_UpdatesUser() {
        String userId = TestDataFactory.TEST_USER_ID;
        PatchUserRequestDto request = TestDataFactory.createPatchUserRequestDto();
        User mockUser = TestDataFactory.createTestUserEntity(userId, false);
        Instant originalUpdatedAt = mockUser.getUpdatedAt();

        // set the profile image url to an empty string in the request
        request.setProfileImageUrl("");

        // set the first name, last name, and phone number to null in the request
        request.setFirstName(null);
        request.setLastName(null);
        request.setPhoneNumber(null);

        // mock the user repository response
        Mockito.when(mockUserRepository.findById(UUID.fromString(userId))).thenReturn(
            Optional.of(mockUser)
        );

        // mock the response from the repository to update the user
        Mockito.when(mockUserRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        try {
            UserResponseDto response = userService.updateUser(userId, request);

            // validate response
            Assertions.assertNotNull(response);
            Assertions.assertEquals(userId, response.getUserId());
            Assertions.assertEquals(TestDataFactory.TEST_USER_USERNAME, response.getUsername());
            Assertions.assertEquals(TestDataFactory.TEST_USER_EMAIL, response.getEmail());
            Assertions.assertEquals(mockUser.getCreatedAt(), response.getCreatedAt());

            // validate changed values (expect profile image to be null)
            Assertions.assertNull(response.getProfileImageUrl());

            // validate first name, last name, and phone number not changed
            Assertions.assertEquals(mockUser.getUserProfile().getFirstName(), response.getFirstName());
            Assertions.assertEquals(mockUser.getUserProfile().getLastName(), response.getLastName());
            Assertions.assertEquals(mockUser.getUserProfile().getPhoneNumber(), response.getPhoneNumber());

            // validate updated at changed
            Assertions.assertTrue(response.getUpdatedAt().isAfter(originalUpdatedAt));
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }

        // validate the repository calls were made
        Mockito.verify(mockUserRepository, Mockito.times(1)).findById(UUID.fromString(userId));
        Mockito.verify(mockUserRepository, Mockito.times(1)).save(any());
    }

    /**
     * Test the updateUser method does not allow the first name property to be set to an empty string.
     */
    @Test
    public void testUpdateUser_InvalidEmptyFirstNameRequest_ThrowsException() {
        String userId = TestDataFactory.TEST_USER_ID;
        PatchUserRequestDto request = TestDataFactory.createPatchUserRequestDto();
        User mockUser = TestDataFactory.createTestUserEntity(userId, false);
        Instant originalUpdatedAt = mockUser.getUpdatedAt();

        // set the first name to an empty string in the request
        request.setFirstName("");

        // set the last name, phone number, and profile image url to null in the request
        request.setLastName(null);
        request.setPhoneNumber(null);
        request.setProfileImageUrl(null);

        // mock the user repository response
        Mockito.when(mockUserRepository.findById(UUID.fromString(userId))).thenReturn(
            Optional.of(mockUser)
        );

        // mock the response from the repository to update the user
        Mockito.when(mockUserRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        try {
            UserResponseDto response = userService.updateUser(userId, request);
            Assertions.fail("Expected exception not thrown");
        } catch (Exception ex) {
            Assertions.assertEquals("First name cannot be empty", ex.getMessage());
        }

        // validate the repository calls were made
        Mockito.verify(mockUserRepository, Mockito.times(1)).findById(UUID.fromString(userId));
        Mockito.verify(mockUserRepository, Mockito.never()).save(any());
    }

    /**
     * Test the updateUser method does not allow the last name property to be set to an empty string.
     */
    @Test
    public void testUpdateUser_InvalidEmptyLastNameRequest_ThrowsException() {
        String userId = TestDataFactory.TEST_USER_ID;
        PatchUserRequestDto request = TestDataFactory.createPatchUserRequestDto();
        User mockUser = TestDataFactory.createTestUserEntity(userId, false);
        Instant originalUpdatedAt = mockUser.getUpdatedAt();

        // set the last name to an empty string in the request
        request.setLastName("");

        // set the first name, phone number, and profile image url to null in the request
        request.setFirstName(null);
        request.setPhoneNumber(null);
        request.setProfileImageUrl(null);

        // mock the user repository response
        Mockito.when(mockUserRepository.findById(UUID.fromString(userId))).thenReturn(
            Optional.of(mockUser)
        );

        // mock the response from the repository to update the user
        Mockito.when(mockUserRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        try {
            UserResponseDto response = userService.updateUser(userId, request);
            Assertions.fail("Expected exception not thrown");
        } catch (Exception ex) {
            Assertions.assertEquals("Last name cannot be empty", ex.getMessage());
        }

        // validate the repository calls were made
        Mockito.verify(mockUserRepository, Mockito.times(1)).findById(UUID.fromString(userId));
        Mockito.verify(mockUserRepository, Mockito.never()).save(any());
    }

    /**
     * Test the updateUser method does not allow the phone number property to be set to an empty string.
     */
    @Test
    public void testUpdateUser_InvalidEmptyPhoneNumberRequest_ThrowsException() {
        String userId = TestDataFactory.TEST_USER_ID;
        PatchUserRequestDto request = TestDataFactory.createPatchUserRequestDto();
        User mockUser = TestDataFactory.createTestUserEntity(userId, false);
        Instant originalUpdatedAt = mockUser.getUpdatedAt();

        // set the phone number to an empty string in the request
        request.setPhoneNumber("");

        // set the first name, last name, and profile image url to null in the request
        request.setFirstName(null);
        request.setLastName(null);
        request.setProfileImageUrl(null);

        // mock the user repository response
        Mockito.when(mockUserRepository.findById(UUID.fromString(userId))).thenReturn(
            Optional.of(mockUser)
        );

        // mock the response from the repository to update the user
        Mockito.when(mockUserRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        try {
            UserResponseDto response = userService.updateUser(userId, request);
            Assertions.fail("Expected exception not thrown");
        } catch (Exception ex) {
            Assertions.assertEquals("Phone number cannot be empty", ex.getMessage());
        }

        // validate the repository calls were made
        Mockito.verify(mockUserRepository, Mockito.times(1)).findById(UUID.fromString(userId));
        Mockito.verify(mockUserRepository, Mockito.never()).save(any());
    }

    /**
     * Test the updateUser method does not allow the email to be changed.
     */
    @Test
    public void testUpdateUser_InvalidEmailChangeRequest_ThrowsException() {
        String userId = TestDataFactory.TEST_USER_ID;
        PatchUserRequestDto request = TestDataFactory.createPatchUserRequestDto();
        User mockUser = TestDataFactory.createTestUserEntity(userId, false);
        Instant originalUpdatedAt = mockUser.getUpdatedAt();

        // set the email to a different email in the request
        request.setEmail("updated_" + TestDataFactory.TEST_USER_EMAIL);

        // set all other fields in the request to null
        request.setFirstName(null);
        request.setLastName(null);
        request.setPhoneNumber(null);
        request.setProfileImageUrl(null);

        // mock the user repository response
        Mockito.when(mockUserRepository.findById(UUID.fromString(userId))).thenReturn(
            Optional.of(mockUser)
        );

        // mock the response from the repository to update the user
        Mockito.when(mockUserRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        try {
            userService.updateUser(userId, request);
            Assertions.fail("Expected exception not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Cannot change username or email", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }

        // validate the repository calls were not made
        Mockito.verify(mockUserRepository, Mockito.never()).findById(UUID.fromString(userId));
        Mockito.verify(mockUserRepository, Mockito.never()).save(any());
    }

    /**
     * Test the updateUser method does not allow the username to be changed.
     */
    @Test
    public void testUpdateUser_InvalidUsernameChangeRequest_ThrowsException() {
        String userId = TestDataFactory.TEST_USER_ID;
        PatchUserRequestDto request = TestDataFactory.createPatchUserRequestDto();
        User mockUser = TestDataFactory.createTestUserEntity(userId, false);
        Instant originalUpdatedAt = mockUser.getUpdatedAt();

        // set the username to a different username in the request
        request.setUsername("updated_" + TestDataFactory.TEST_USER_USERNAME);

        // set all other fields in the request to null
        request.setFirstName(null);
        request.setLastName(null);
        request.setPhoneNumber(null);
        request.setProfileImageUrl(null);

        // mock the user repository response
        Mockito.when(mockUserRepository.findById(UUID.fromString(userId))).thenReturn(
            Optional.of(mockUser)
        );

        // mock the response from the repository to update the user
        Mockito.when(mockUserRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        try {
            userService.updateUser(userId, request);
            Assertions.fail("Expected exception not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Cannot change username or email", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }

        // validate the repository calls were not made
        Mockito.verify(mockUserRepository, Mockito.never()).findById(UUID.fromString(userId));
        Mockito.verify(mockUserRepository, Mockito.never()).save(any());
    }

    /**
     * Test that the updateUser method throws an exception with an empty userId.
     */
    @Test
    public void testUpdateUser_InvalidEmptyUserIdRequest_ThrowsException() {
        String userId = "";
        PatchUserRequestDto request = TestDataFactory.createPatchUserRequestDto();
        User mockUser = TestDataFactory.createTestUserEntity(TestDataFactory.TEST_USER_ID, false);
        Instant originalUpdatedAt = mockUser.getUpdatedAt();

        try {
            userService.updateUser(userId, request);
            Assertions.fail("Expected exception not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }

        // validate the repository calls were not made
        Mockito.verify(mockUserRepository, Mockito.never()).findById(any());
        Mockito.verify(mockUserRepository, Mockito.never()).save(any());
    }

    /**
     * Test that the updateUser method throws an exception with a null userId.
     */
    @Test
    public void testUpdateUser_InvalidNullUserIdRequest_ThrowsException() {
        String userId = null;
        PatchUserRequestDto request = TestDataFactory.createPatchUserRequestDto();
        User mockUser = TestDataFactory.createTestUserEntity(TestDataFactory.TEST_USER_ID, false);
        Instant originalUpdatedAt = mockUser.getUpdatedAt();

        try {
            userService.updateUser(userId, request);
            Assertions.fail("Expected exception not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }

        // validate the repository calls were not made
        Mockito.verify(mockUserRepository, Mockito.never()).findById(any());
        Mockito.verify(mockUserRepository, Mockito.never()).save(any());
    }

}

