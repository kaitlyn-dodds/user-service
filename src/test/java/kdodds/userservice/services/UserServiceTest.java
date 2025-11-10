package kdodds.userservice.services;

import kdodds.userservice.dto.requests.CreateUserRequestDto;
import kdodds.userservice.dto.responses.UserAddressResponseDto;
import kdodds.userservice.dto.responses.UserAddressesResponseDto;
import kdodds.userservice.dto.responses.UserProfileResponseDto;
import kdodds.userservice.dto.responses.UserResponseDto;
import kdodds.userservice.exceptions.models.exceptions.InvalidRequestDataException;
import kdodds.userservice.exceptions.models.exceptions.InvalidUserIdException;
import kdodds.userservice.exceptions.models.exceptions.UserAddressNotFound;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
     * Test the getUserAddressesDtoByUserId method returns a valid list of UserAddressDtos when a user exists.
     */
    @Test
    public void testGetUserAddressesDtoByUserId_UserExists() throws Exception {
        String userId = TestDataFactory.TEST_USER_ID;

        // mock user address repository response
        Mockito.when(mockUserAddressRepository.findAddressesByUserId(UUID.fromString(userId))).thenReturn(
            Optional.of(
                List.of(TestDataFactory.createTestUserAddressEntity(userId))
            )
        );

        UserAddressesResponseDto response = userService.getUserAddressesDtoByUserId(userId);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(userId, response.getUserId());

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
     * Test the getUserAddressesDtoByUserId method throws an InvalidUserIdException when the user id is empty.
     */
    @Test
    public void testGetUserAddressesDtoByUserId_MissingId_ThrowsInvalidUserIdException() {
        String userId = "";

        try {
            userService.getUserAddressesDtoByUserId(userId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the getUserAddressesDtoByUserId method throws an InvalidUserIdException when the user id is null.
     */
    @Test
    public void testGetUserAddressesDtoByUserId_NullId_ThrowsInvalidUserIdException() {
        String userId = null;

        try {
            userService.getUserAddressesDtoByUserId(userId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the getUserAddressesDtoByUserId method throws an exception when the call to the user address repository
     * fails.
     */
    @Test
    public void testGetUserAddressesDtoByUserId_RepositoryCallFails_ThrowsException() {
        String userId = TestDataFactory.TEST_USER_ID;

        // mock user address repository response
        Mockito.when(mockUserAddressRepository.findAddressesByUserId(UUID.fromString(userId))).thenThrow(
            new RuntimeException("mock exception")
        );

        try {
            userService.getUserAddressesDtoByUserId(userId);
            Assertions.fail("Expected exception not thrown");
        } catch (Exception ex) {
            Assertions.assertEquals(
                String.format("Find addresses by user id for userId %s failed for unknown reasons", userId),
                ex.getMessage()
            );
        }
    }

    /**
     * Test the getUserAddressesDtoByUserId method returns an empty list when the user does not have any addresses.
     */
    @Test
    public void testGetUserAddressesDtoByUserId_UserHasNoAddresses_ReturnsEmptyList() throws Exception {
        String userId = TestDataFactory.TEST_USER_ID;

        // mock user address repository response
        Mockito.when(mockUserAddressRepository.findAddressesByUserId(UUID.fromString(userId))).thenReturn(
            Optional.empty()
        );

        UserAddressesResponseDto response = userService.getUserAddressesDtoByUserId(userId);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(userId, response.getUserId());
        Assertions.assertNotNull(response.getAddresses());
        Assertions.assertEquals(0, response.getAddresses().size());
    }

    /**
     * Test the getUserAddressById method returns a valid UserAddressDto when a user exists.
     */
    @Test
    public void testGetUserAddressDtoById_ValidAddressId_ReturnsUserAddressDto() throws Exception {
        String userId = TestDataFactory.TEST_USER_ID;
        String addressId = TestDataFactory.TEST_ADDRESS_ID_1;

        // mock user address repository response
        Mockito.when(mockUserAddressRepository.findById(UUID.fromString(addressId))).thenReturn(
            Optional.of(TestDataFactory.createTestUserAddressEntity(userId))
        );

        UserAddressResponseDto response = userService.getUserAddressDtoById(userId, addressId);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(userId, response.getUserId());
        Assertions.assertEquals(addressId, response.getAddressId());
        Assertions.assertEquals(TestDataFactory.TEST_USER_ADDRESS_LINE_1, response.getAddressLine1());
        Assertions.assertEquals(TestDataFactory.TEST_USER_ADDRESS_TYPE, response.getAddressType());
        Assertions.assertEquals(TestDataFactory.TEST_USER_CITY, response.getCity());
        Assertions.assertEquals(TestDataFactory.TEST_USER_STATE, response.getState());
        Assertions.assertEquals(TestDataFactory.TEST_USER_ZIP_CODE, response.getZipCode());
        Assertions.assertEquals(TestDataFactory.TEST_USER_COUNTRY, response.getCountry());
        Assertions.assertNotNull(response.getCreatedAt());
        Assertions.assertNotNull(response.getUpdatedAt());
    }

    /**
     * Test the getUserAddressById method throws an InvalidRequestDataException when the user id is empty.
     */
    @Test
    public void testGetUserAddressDtoById_MissingUserId_ThrowsInvalidUserIdException() {
        String userId = "";
        String addressId = TestDataFactory.TEST_ADDRESS_ID_1;

        try {
            userService.getUserAddressDtoById(userId, addressId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Invalid request data.", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the getUserAddressById method throws an InvalidRequestDataException when the user id is null.
     */
    @Test
    public void testGetUserAddressDtoById_NullUserId_ThrowsInvalidUserIdException() {
        String userId = null;
        String addressId = TestDataFactory.TEST_ADDRESS_ID_1;

        try {
            userService.getUserAddressDtoById(userId, addressId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Invalid request data.", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the getUserAddressById method throws an InvalidRequestDataException when the address id is empty.
     */
    @Test
    public void testGetUserAddressDtoById_MissingAddressId_ThrowsInvalidRequestDataException() {
        String userId = TestDataFactory.TEST_USER_ID;
        String addressId = "";

        try {
            userService.getUserAddressDtoById(userId, addressId);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Invalid request data.", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the getUserAddressById method throws an InvalidRequestDataException when the address id is null.
     */
    @Test
    public void testGetUserAddressDtoById_NullAddressId_ThrowsInvalidRequestDataException() {
        String userId = TestDataFactory.TEST_USER_ID;
        String addressId = null;

        try {
            userService.getUserAddressDtoById(userId, addressId);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Invalid request data.", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the getUserAddressById method throws an exception when the call to the user address repository fails.
     */
    @Test
    public void testGetUserAddressDtoById_RepositoryCallFails_ThrowsException() {
        String userId = TestDataFactory.TEST_USER_ID;
        String addressId = TestDataFactory.TEST_ADDRESS_ID_1;

        // mock user address repository response
        Mockito.when(mockUserAddressRepository.findById(UUID.fromString(addressId))).thenThrow(
            new RuntimeException("mock exception")
        );

        try {
            userService.getUserAddressDtoById(userId, addressId);
            Assertions.fail("Expected exception not thrown");
        } catch (Exception ex) {
            Assertions.assertEquals(
                String.format(
                    "Find address by id for userId %s and addressId %s failed for unknown reasons",
                    userId,
                    addressId
                ),
                ex.getMessage()
            );
        }
    }

    /**
     * Test the getUserAddressDtoById method throws a UserAddressNotFoundException when the user does not have the
     * specified address.
     */
    @Test
    public void testGetUserAddressDtoById_UserDoesNotHaveAddress_ThrowsUserAddressNotFoundException() {
        String userId = TestDataFactory.TEST_USER_ID;
        String addressId = TestDataFactory.TEST_ADDRESS_ID_1;

        // mock user address repository response
        Mockito.when(mockUserAddressRepository.findById(UUID.fromString(addressId))).thenReturn(
            Optional.empty()
        );

        try {
            userService.getUserAddressDtoById(userId, addressId);
            Assertions.fail("Expected UserAddressNotFound not thrown");
        } catch (UserAddressNotFound ex) {
            Assertions.assertEquals(
                String.format(
                    "Find address by id for userId %s and addressId %s failed for unknown reasons",
                    userId,
                    addressId
                ), ex.getMessage());
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

}

