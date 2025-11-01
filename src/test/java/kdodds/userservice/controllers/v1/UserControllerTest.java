package kdodds.userservice.controllers.v1;

import kdodds.userservice.assemblers.PagedUsersModelAssembler;
import kdodds.userservice.assemblers.UserModelAssembler;
import kdodds.userservice.dto.responses.PagedUsersResponseDto;
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

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
public class UserControllerTest {

    @Mock
    private UserModelAssembler mockUserModelAssembler;

    @Mock
    private UserService mockUserService;

    @Mock
    private PagedUsersModelAssembler mockPagedUsersModelAssembler;

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

        // mock the paged users model assembler to return the input wrapped in an EntityModel
        Mockito.when(mockPagedUsersModelAssembler.toModel(Mockito.any(PagedUsersResponseDto.class)))
            .thenAnswer(invocation -> {
                PagedUsersResponseDto argPagedUsersDto = invocation.getArgument(0);
                return EntityModel.of(argPagedUsersDto);
            });
    }

    /**
     * Test the UserController /users endpoint returns a 200 and a list of users.
     */
    @Test
    public void testGetUsers_ReturnsPagedUsersResponse() throws Exception {
        int page = 0;
        int size = 10;
        int totalPages = 1;
        int totalElements = 1;

        // mock the service call
        Mockito.when(mockUserService.getAllUsersPaginated(page, size)).thenReturn(
            TestDataFactory.createTestPagedUsersResponseDto(
                List.of(TestDataFactory.createTestUserResponseDto(TestDataFactory.TEST_USER_ID)),
                TestDataFactory.createTestPageDto(page, size, totalPages, totalElements)
            )
        );

        ResponseEntity<EntityModel<PagedUsersResponseDto>> response = userController.getAllUsersPaginated(page, size);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());

        // validate paged users response
        Assertions.assertNotNull(response.getBody());
        PagedUsersResponseDto pagedUsersResponse = response.getBody().getContent();
        Assertions.assertNotNull(pagedUsersResponse);
        Assertions.assertEquals(page, pagedUsersResponse.getPage().getPage());
        Assertions.assertEquals(size, pagedUsersResponse.getPage().getSize());
        Assertions.assertEquals(totalElements, pagedUsersResponse.getPage().getTotalElements());
        Assertions.assertEquals(totalPages, pagedUsersResponse.getPage().getTotalPages());

        // validate users in paged users response
        Assertions.assertNotNull(pagedUsersResponse.getUsers());
        Assertions.assertEquals(1, pagedUsersResponse.getUsers().size());
        Assertions.assertEquals(TestDataFactory.TEST_USER_ID, pagedUsersResponse.getUsers().getFirst().getUserId());
        Assertions.assertEquals(
            TestDataFactory.TEST_USER_USERNAME, pagedUsersResponse.getUsers().getFirst().getUsername());
        Assertions.assertEquals(TestDataFactory.TEST_USER_EMAIL, pagedUsersResponse.getUsers().getFirst().getEmail());
        Assertions.assertEquals(
            TestDataFactory.TEST_USER_FIRST_NAME, pagedUsersResponse.getUsers().getFirst().getFirstName());
        Assertions.assertEquals(
            TestDataFactory.TEST_USER_LAST_NAME, pagedUsersResponse.getUsers().getFirst().getLastName());
        Assertions.assertEquals(
            TestDataFactory.TEST_USER_PHONE_NUMBER, pagedUsersResponse.getUsers().getFirst().getPhoneNumber());
        Assertions.assertEquals(
            TestDataFactory.TEST_USER_PROFILE_IMAGE_URL, pagedUsersResponse.getUsers().getFirst().getProfileImageUrl());
        Assertions.assertNotNull(pagedUsersResponse.getUsers().getFirst().getCreatedAt());
        Assertions.assertNotNull(pagedUsersResponse.getUsers().getFirst().getUpdatedAt());
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
