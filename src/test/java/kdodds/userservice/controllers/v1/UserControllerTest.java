package kdodds.userservice.controllers.v1;

import kdodds.userservice.assemblers.UserModelAssembler;
import kdodds.userservice.dto.responses.UserResponseDto;
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
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public class UserControllerTest {

    @Mock
    private UserModelAssembler mockUserModelAssembler;

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
        Mockito.reset(
            mockUserService,
            mockUserModelAssembler
        );

        // mock the user model assembler to just return the input wrapped in an EntityModel
        Mockito.when(mockUserModelAssembler.toModel(Mockito.any(UserResponseDto.class)))
            .thenAnswer(invocation -> {
                UserResponseDto argUserDto = invocation.getArgument(0);
                return EntityModel.of(argUserDto);
            });
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

        ResponseEntity<EntityModel<UserResponseDto>> response = userController.getUserByUserId(userId);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());

        // validate user and user profile
        Assertions.assertNotNull(response.getBody());
        UserResponseDto userResponse = response.getBody().getContent();
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

}
