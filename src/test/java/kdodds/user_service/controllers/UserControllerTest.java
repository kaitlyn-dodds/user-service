package kdodds.user_service.controllers;

import kdodds.user_service.models.responses.UserAddressesResponse;
import kdodds.user_service.models.responses.UserProfileResponse;
import kdodds.user_service.models.responses.UserResponse;
import kdodds.user_service.services.UserService;
import kdodds.user_service.utils.TestDataFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

@SpringBootTest
public class UserControllerTest {

    @Mock
    private UserService mockUserService;

    @InjectMocks
    private UserController userController;

    /**
     * Setup for each test.
     */
    @BeforeEach
    public void setup() {
        // clear mocks
        Mockito.reset(mockUserService);
    }

    /**
     * Test the UserController /users/{userId} endpoint returns a 200 status code along w/ a complete UserResponse.
     */
    @Test
    public void testGetUserById_ValidId_ReturnsUserResponse() {
        String userId = TestDataFactory.TEST_USER_ID;

        // mock the service call
        Mockito.when(mockUserService.getCompleteUserDataByUserId(userId)).thenAnswer(invocation -> {
            // get the user id arg
            String argUserId = invocation.getArgument(0);

            // create and return a complete user data object
            return TestDataFactory.createTestCompleteUserData(argUserId);
        });

        ResponseEntity<UserResponse> response = userController.getUserByUserId(userId);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());

        // validate user and user profile
        UserResponse userResponse = response.getBody();
        Assertions.assertNotNull(userResponse);
        Assertions.assertEquals(userId, userResponse.getUserId());
        Assertions.assertEquals(TestDataFactory.TEST_USER_USERNAME, userResponse.getUsername());
        Assertions.assertEquals(TestDataFactory.TEST_USER_EMAIL, userResponse.getEmail());
        Assertions.assertEquals(TestDataFactory.TEST_USER_FIRST_NAME, userResponse.getFirstName());
        Assertions.assertEquals(TestDataFactory.TEST_USER_LAST_NAME, userResponse.getLastName());
        Assertions.assertEquals(TestDataFactory.TEST_USER_PHONE_NUMBER, userResponse.getPhoneNumber());
        Assertions.assertEquals(TestDataFactory.TEST_USER_PROFILE_IMAGE_URL, userResponse.getProfileImageUrl());
        Assertions.assertNotNull(userResponse.getCreatedAt());
        Assertions.assertNotNull(userResponse.getUpdatedAt());

        // validate user addresses
        Assertions.assertNotNull(userResponse.getAddresses());
        Assertions.assertEquals(1, userResponse.getAddresses().size());
        Assertions.assertEquals(
            TestDataFactory.TEST_USER_ADDRESS_LINE_1,
            userResponse.getAddresses().getFirst().getAddressLine1()
        );
        Assertions.assertEquals(
            TestDataFactory.TEST_USER_ADDRESS_TYPE,
            userResponse.getAddresses().getFirst().getAddressType()
        );
        Assertions.assertEquals(TestDataFactory.TEST_USER_CITY, userResponse.getAddresses().getFirst().getCity());
        Assertions.assertEquals(TestDataFactory.TEST_USER_STATE, userResponse.getAddresses().getFirst().getState());
        Assertions.assertEquals(
            TestDataFactory.TEST_USER_ZIP_CODE,
            userResponse.getAddresses().getFirst().getZipCode()
        );
        Assertions.assertEquals(TestDataFactory.TEST_USER_COUNTRY, userResponse.getAddresses().getFirst().getCountry());
        Assertions.assertNotNull(userResponse.getAddresses().getFirst().getCreatedAt());
        Assertions.assertNotNull(userResponse.getAddresses().getFirst().getUpdatedAt());
    }

    /**
     * Test the UserController /users/{userId} endpoint throws an InvalidUserIdException when the user id is empty.
     */
    @Test
    public void testGetUserById_MissingId_ThrowsInvalidUserIdException() {
        String userId = "";

        try {
            userController.getUserByUserId(userId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (Exception ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        }
    }

    /**
     * Test the UserController /users/{userId} endpoint throws an InvalidUserIdException when the user id is null.
     */
    @Test
    public void testGetUserById_NullId_ThrowsInvalidUserIdException() {
        String userId = null;

        try {
            userController.getUserByUserId(userId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (Exception ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        }
    }

    /**
     * Test the UserController /users/{userId}/profile endpoint returns a 200 status code along w/ a complete
     * UserProfileResponse.
     */
    @Test
    public void testGetUserProfileById_ValidId_ReturnsUserProfileResponse() {
        String userId = TestDataFactory.TEST_USER_ID;

        // mock the service call
        Mockito.when(mockUserService.getUserProfileByUserId(userId)).thenReturn(
            TestDataFactory.createTestUserProfile(userId)
        );

        ResponseEntity<UserProfileResponse> response = userController.getUserProfileByUserId(userId);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());

        // validate user profile
        UserProfileResponse userProfileResponse = response.getBody();
        Assertions.assertNotNull(userProfileResponse);
        Assertions.assertEquals(userId, userProfileResponse.getUserId());
        Assertions.assertEquals(TestDataFactory.TEST_USER_FIRST_NAME, userProfileResponse.getFirstName());
        Assertions.assertEquals(TestDataFactory.TEST_USER_LAST_NAME, userProfileResponse.getLastName());
        Assertions.assertEquals(TestDataFactory.TEST_USER_PHONE_NUMBER, userProfileResponse.getPhoneNumber());
        Assertions.assertEquals(TestDataFactory.TEST_USER_PROFILE_IMAGE_URL, userProfileResponse.getProfileImageUrl());
        Assertions.assertNotNull(userProfileResponse.getCreatedAt());
        Assertions.assertNotNull(userProfileResponse.getUpdatedAt());
    }

    /**
     * Test the UserController /users/{userId}/profile endpoint throws an InvalidUserIdException when the user id is
     * empty.
     */
    @Test
    public void testGetUserProfileById_MissingId_ThrowsInvalidUserIdException() {
        String userId = "";

        try {
            userController.getUserProfileByUserId(userId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (Exception ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        }
    }

    /**
     * Test the UserController /users/{userId}/profile endpoint throws an InvalidUserIdException when the user id is
     * null.
     */
    @Test
    public void testGetUserProfileById_NullId_ThrowsInvalidUserIdException() {
        String userId = null;

        try {
            userController.getUserProfileByUserId(userId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (Exception ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        }
    }

    /**
     * Test the UserController /users/{userId}/addresses endpoint returns a 200 status code along w/ a list of
     * UserAddresses.
     */
    @Test
    public void testGetUserAddressesById_ValidId_ReturnsUserAddresses() {
        String userId = TestDataFactory.TEST_USER_ID;

        // mock service call
        Mockito.when(mockUserService.getUserAddressesByUserId(userId)).thenReturn(
            List.of(TestDataFactory.createTestUserAddress(userId))
        );

        ResponseEntity<UserAddressesResponse> response = userController.getUserAddressesByUserId(userId);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());

        // validate addresses
        UserAddressesResponse userAddressesResponse = response.getBody();
        Assertions.assertNotNull(userAddressesResponse);
        Assertions.assertEquals(userId, userAddressesResponse.getUserId());
        Assertions.assertNotNull(userAddressesResponse.getAddresses());
        Assertions.assertEquals(1, userAddressesResponse.getAddresses().size());
        Assertions.assertEquals(
            TestDataFactory.TEST_USER_ADDRESS_LINE_1,
            userAddressesResponse.getAddresses().getFirst().getAddressLine1()
        );
        Assertions.assertEquals(
            TestDataFactory.TEST_USER_ADDRESS_TYPE,
            userAddressesResponse.getAddresses().getFirst().getAddressType()
        );
        Assertions.assertEquals(TestDataFactory.TEST_USER_CITY, userAddressesResponse.getAddresses().getFirst().getCity());
        Assertions.assertEquals(TestDataFactory.TEST_USER_STATE, userAddressesResponse.getAddresses().getFirst().getState());
        Assertions.assertEquals(
            TestDataFactory.TEST_USER_ZIP_CODE,
            userAddressesResponse.getAddresses().getFirst().getZipCode()
        );
        Assertions.assertEquals(TestDataFactory.TEST_USER_COUNTRY, userAddressesResponse.getAddresses().getFirst().getCountry());
        Assertions.assertNotNull(userAddressesResponse.getAddresses().getFirst().getCreatedAt());
        Assertions.assertNotNull(userAddressesResponse.getAddresses().getFirst().getUpdatedAt());
    }

    /**
     * Test the UserController /users/{userId}/addresses endpoint throws an InvalidUserIdException when the user id is
     * empty.
     */
    @Test
    public void testGetUserAddressesById_MissingId_ThrowsInvalidUserIdException() {
        String userId = "";

        try {
            userController.getUserAddressesByUserId(userId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (Exception ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        }
    }

    /**
     * Test the UserController /users/{userId}/addresses endpoint throws an InvalidUserIdException when the user id is
     * null.
     */
    @Test
    public void testGetUserAddressesById_NullId_ThrowsInvalidUserIdException() {
        String userId = null;

        try {
            userController.getUserAddressesByUserId(userId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (Exception ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        }
    }

}
