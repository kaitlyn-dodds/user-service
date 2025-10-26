package kdodds.userservice.services;

import kdodds.userservice.dto.responses.UserResponseDto;
import kdodds.userservice.exceptions.models.exceptions.InvalidUserIdException;
import kdodds.userservice.exceptions.models.exceptions.UserNotFoundException;
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
            Optional.of(TestDataFactory.createTestUserEntity(userId))
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

}

