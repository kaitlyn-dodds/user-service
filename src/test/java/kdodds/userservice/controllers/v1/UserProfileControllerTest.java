package kdodds.userservice.controllers.v1;

import kdodds.userservice.assemblers.UserProfileModelAssembler;
import kdodds.userservice.dto.responses.UserProfileResponseDto;
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

@SpringBootTest
public class UserProfileControllerTest {

    @Mock
    private UserService mockUserService;

    @Mock
    private UserProfileModelAssembler mockUserProfileModelAssembler;

    @InjectMocks
    private UserProfileController userProfileController;

    /**
     * Setup for each test.
     */
    @BeforeEach
    public void setup() {
        // clear mocks
        Mockito.reset(mockUserProfileModelAssembler, mockUserService);

        // mock the user profile model assembler to just return the input wrapped in an EntityModel
        Mockito.when(mockUserProfileModelAssembler.toModel(Mockito.any(UserProfileResponseDto.class)))
            .thenAnswer(invocation -> {
                UserProfileResponseDto argUserProfileDto = invocation.getArgument(0);
                return EntityModel.of(argUserProfileDto);
            });
    }

    /**
     * Test the UserProfileController /users/{userId}/profile endpoint returns a 200 status code along w/ a complete
     * UserProfileResponse.
     */
    @Test
    public void testGetUserProfileById_ValidId_ReturnsUserProfileResponse() throws Exception {
        String userId = TestDataFactory.TEST_USER_ID;

        // mock the service call
        Mockito.when(mockUserService.getUserProfileDtoByUserId(userId)).thenReturn(
            TestDataFactory.createTestUserProfileDto(userId)
        );

        ResponseEntity<EntityModel<UserProfileResponseDto>> response =
            userProfileController.getUserProfileByUserId(userId);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());

        // validate user profile
        Assertions.assertNotNull(response.getBody());
        UserProfileResponseDto userProfileResponse = response.getBody().getContent();
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
     * Test the UserProfileController /users/{userId}/profile endpoint throws an InvalidUserIdException when the user
     * id is empty.
     */
    @Test
    public void testGetUserProfileById_MissingId_ThrowsInvalidUserIdException() {
        String userId = "";

        try {
            userProfileController.getUserProfileByUserId(userId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the UserProfileController /users/{userId}/profile endpoint throws an InvalidUserIdException when the user
     * id is null.
     */
    @Test
    public void testGetUserProfileById_NullId_ThrowsInvalidUserIdException() {
        String userId = null;

        try {
            userProfileController.getUserProfileByUserId(userId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

}
