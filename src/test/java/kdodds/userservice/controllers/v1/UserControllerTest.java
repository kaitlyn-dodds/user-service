package kdodds.userservice.controllers.v1;

import kdodds.userservice.dto.responses.UserAddressResponseDto;
import kdodds.userservice.dto.responses.UserAddressesResponseDto;
import kdodds.userservice.dto.responses.UserProfileResponseDto;
import kdodds.userservice.dto.responses.UserResponseDto;
import kdodds.userservice.exceptions.models.exceptions.InvalidRequestDataException;
import kdodds.userservice.exceptions.models.exceptions.InvalidUserIdException;
import kdodds.userservice.services.UserService;
import kdodds.userservice.utils.TestDataFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test")
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
    public void testGetUserById_ValidId_ReturnsUserResponse() throws Exception {
        String userId = TestDataFactory.TEST_USER_ID;

        // mock the service call
        Mockito.when(mockUserService.getUserResponseDto(userId)).thenAnswer(invocation -> {
            // get the user id arg
            String argUserId = invocation.getArgument(0);

            // create and return a complete user data object
            return TestDataFactory.createTestUserResponseDto(argUserId);
        });

        ResponseEntity<UserResponseDto> response = userController.getUserByUserId(userId);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());

        // validate user and user profile
        UserResponseDto userResponse = response.getBody();
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
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
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
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the UserController /users/{userId}/profile endpoint returns a 200 status code along w/ a complete
     * UserProfileResponse.
     */
    @Test
    public void testGetUserProfileById_ValidId_ReturnsUserProfileResponse() throws Exception {
        String userId = TestDataFactory.TEST_USER_ID;

        // mock the service call
        Mockito.when(mockUserService.getUserProfileDtoByUserId(userId)).thenReturn(
            TestDataFactory.createTestUserProfileDto(userId)
        );

        ResponseEntity<UserProfileResponseDto> response = userController.getUserProfileByUserId(userId);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());

        // validate user profile
        UserProfileResponseDto userProfileResponse = response.getBody();
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
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
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
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the UserController /users/{userId}/addresses endpoint returns a 200 status code along w/ a list of
     * UserAddresses.
     */
    @Test
    public void testGetUserAddressesById_ValidId_ReturnsUserAddresses() throws Exception {
        String userId = TestDataFactory.TEST_USER_ID;

        // mock service call
        Mockito.when(mockUserService.getUserAddressesDtoByUserId(userId)).thenReturn(
            UserAddressesResponseDto.builder()
                .userId(userId)
                .addresses(List.of(TestDataFactory.createTestUserAddressDto(userId)))
                .build()
        );

        ResponseEntity<UserAddressesResponseDto> response = userController.getUserAddressesByUserId(userId);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());

        // validate addresses
        UserAddressesResponseDto userAddressesResponse = response.getBody();
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
        Assertions.assertEquals(TestDataFactory.TEST_USER_CITY,
            userAddressesResponse.getAddresses().getFirst().getCity()
        );
        Assertions.assertEquals(TestDataFactory.TEST_USER_STATE,
            userAddressesResponse.getAddresses().getFirst().getState()
        );
        Assertions.assertEquals(
            TestDataFactory.TEST_USER_ZIP_CODE,
            userAddressesResponse.getAddresses().getFirst().getZipCode()
        );
        Assertions.assertEquals(TestDataFactory.TEST_USER_COUNTRY,
            userAddressesResponse.getAddresses().getFirst().getCountry()
        );
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
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
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
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the UserController /users/{userId}/addresses/{addressId} endpoint returns a 200 status code along w/ a
     * complete UserAddress.
     */
    @Test
    public void testGetUserAddressById_ValidIds_ReturnExpectedAddress() throws Exception {
        String userId = TestDataFactory.TEST_USER_ID;
        String addressId = TestDataFactory.TEST_ADDRESS_ID_1;

        // mock user service response
        Mockito.when(mockUserService.getUserAddressDtoById(userId, addressId)).thenReturn(
            TestDataFactory.createTestUserAddressDto(userId)
        );

        ResponseEntity<UserAddressResponseDto> response = userController.getUserAddressById(userId, addressId);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());

        // validate address
        UserAddressResponseDto userAddressResponse = response.getBody();
        Assertions.assertNotNull(userAddressResponse);
        Assertions.assertEquals(userId, userAddressResponse.getUserId());
        Assertions.assertEquals(TestDataFactory.TEST_USER_ADDRESS_LINE_1, userAddressResponse.getAddressLine1());
        Assertions.assertEquals(TestDataFactory.TEST_USER_ADDRESS_TYPE, userAddressResponse.getAddressType());
        Assertions.assertEquals(TestDataFactory.TEST_USER_CITY, userAddressResponse.getCity());
        Assertions.assertEquals(TestDataFactory.TEST_USER_STATE, userAddressResponse.getState());
        Assertions.assertEquals(TestDataFactory.TEST_USER_ZIP_CODE, userAddressResponse.getZipCode());
        Assertions.assertEquals(TestDataFactory.TEST_USER_COUNTRY, userAddressResponse.getCountry());
        Assertions.assertNotNull(userAddressResponse.getCreatedAt());
        Assertions.assertNotNull(userAddressResponse.getUpdatedAt());
    }

    /**
     * Test the UserController /users/{userId}/addresses/{addressId} endpoint throws an InvalidUserIdException when the
     * user id is empty.
     */
    @Test
    public void testGetUserAddressById_MissingUserId_ThrowsInvalidUserIdException() {
        String userId = "";
        String addressId = TestDataFactory.TEST_ADDRESS_ID_1;

        try {
            userController.getUserAddressById(userId, addressId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the UserController /users/{userId}/addresses/{addressId} endpoint throws an InvalidUserIdException when the
     * user id is null.
     */
    @Test
    public void testGetUserAddressById_NullUserId_ThrowsInvalidUserIdException() {
        String userId = null;
        String addressId = TestDataFactory.TEST_ADDRESS_ID_1;

        try {
            userController.getUserAddressById(userId, addressId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the UserController /users/{userId}/addresses/{addressId} endpoint throws an InvalidRequestData exception
     * when the address id is empty.
     */
    @Test
    public void testGetUserAddressById_MissingAddressId_ThrowsInvalidRequestData() {
        String userId = TestDataFactory.TEST_USER_ID;
        String addressId = "";

        try {
            userController.getUserAddressById(userId, addressId);
            Assertions.fail("Expected InvalidRequestData exception not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Invalid null or empty address id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the UserController /users/{userId}/addresses/{addressId} endpoint throws an InvalidRequestData exception
     * when the address id is null.
     */
    @Test
    public void testGetUserAddressById_NullAddressId_ThrowsInvalidRequestData() {
        String userId = TestDataFactory.TEST_USER_ID;
        String addressId = null;

        try {
            userController.getUserAddressById(userId, addressId);
            Assertions.fail("Expected InvalidRequestData exception not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Invalid null or empty address id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

}
