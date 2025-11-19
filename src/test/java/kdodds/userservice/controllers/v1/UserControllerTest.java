package kdodds.userservice.controllers.v1;

import kdodds.userservice.assemblers.PagedUsersModelAssembler;
import kdodds.userservice.assemblers.UserModelAssembler;
import kdodds.userservice.dto.requests.CreateUserRequestDto;
import kdodds.userservice.dto.requests.PatchUserRequestDto;
import kdodds.userservice.dto.responses.PagedUsersResponseDto;
import kdodds.userservice.dto.responses.UserResponseDto;
import kdodds.userservice.entities.User;
import kdodds.userservice.exceptions.models.exceptions.InvalidRequestDataException;
import kdodds.userservice.exceptions.models.exceptions.InvalidUserIdException;
import kdodds.userservice.exceptions.models.exceptions.UserConflictException;
import kdodds.userservice.services.UserService;
import kdodds.userservice.utils.TestDataFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

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
        Mockito.when(mockUserModelAssembler.toModel(any(UserResponseDto.class)))
            .thenAnswer(invocation -> {
                UserResponseDto argUserDto = invocation.getArgument(0);
                return EntityModel.of(argUserDto);
            });

        // mock the paged users model assembler to return the input wrapped in an EntityModel
        Mockito.when(mockPagedUsersModelAssembler.toModel(any(PagedUsersResponseDto.class)))
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
        Mockito.when(
            mockUserService.getAllUsersPaginated(eq(page), eq(size), ArgumentMatchers.<Specification<User>>any()))
                .thenReturn(
                    TestDataFactory.createTestPagedUsersResponseDto(
                        List.of(TestDataFactory.createTestUserResponseDto(TestDataFactory.TEST_USER_ID)),
                        TestDataFactory.createTestPageDto(page, size, totalPages, totalElements)
                    )
            );

        ResponseEntity<EntityModel<PagedUsersResponseDto>> response =
            userController.getAllUsersPaginated(page, size, null);

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

    /**
     * Test the UserController createUser endpoint returns a 201 Created and complete UserResponseDto when given a valid
     * CreateUserRequestDto.
     */
    @Test
    public void testCreateUser_ValidRequest_ReturnsUserResponse() throws Exception {
        CreateUserRequestDto request = TestDataFactory.createUserRequestDto();

        // mock the service call
        Mockito.when(mockUserService.createUserAndProfileAndAddress(request))
            .thenReturn(TestDataFactory.createTestUserResponseDto());

        // make request
        ResponseEntity<EntityModel<UserResponseDto>> response = userController.createUser(request);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(201, response.getStatusCode().value());

        // validate user and user profile
        Assertions.assertNotNull(response.getBody());
        UserResponseDto userResponse = response.getBody().getContent();
        Assertions.assertNotNull(userResponse);
        Assertions.assertNotNull(userResponse.getUserId());
        Assertions.assertEquals(TestDataFactory.TEST_USER_USERNAME, userResponse.getUsername());
        Assertions.assertEquals(TestDataFactory.TEST_USER_EMAIL, userResponse.getEmail());
        Assertions.assertEquals(TestDataFactory.TEST_USER_FIRST_NAME, userResponse.getFirstName());
        Assertions.assertEquals(TestDataFactory.TEST_USER_LAST_NAME, userResponse.getLastName());
        Assertions.assertEquals(TestDataFactory.TEST_USER_PHONE_NUMBER, userResponse.getPhoneNumber());
        Assertions.assertEquals(TestDataFactory.TEST_USER_PROFILE_IMAGE_URL, userResponse.getProfileImageUrl());
        Assertions.assertNotNull(userResponse.getCreatedAt());
        Assertions.assertNotNull(userResponse.getUpdatedAt());

        // validate address
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
     * Test the UserController createUser endpoint throws an InvalidRequestDataException when the request is null.
     */
    @Test
    public void testCreateUser_NullRequest_ThrowsException() {
        try {
            userController.createUser(null);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Request body must be included", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the UserController createUser endpoint throws an InvalidRequestDataException when the request is missing a
     * username.
     */
    @Test
    public void testCreateUser_IncompleteRequest__MissingUsername_ThrowsException() {
        CreateUserRequestDto request = TestDataFactory.createUserRequestDto();

        // set username to null to make request incomplete
        request.setUsername(null);

        try {
            userController.createUser(request);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Username must be included", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the UserController createUser endpoint throws an InvalidRequestDataException when the request is missing an
     * email.
     */
    @Test
    public void testCreateUser_IncompleteRequest__MissingEmail_ThrowsException() {
        CreateUserRequestDto request = TestDataFactory.createUserRequestDto();

        // set email to null to make request incomplete
        request.setEmail(null);

        try {
            userController.createUser(request);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Email must be included", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the UserController createUser endpoint throws an InvalidRequestDataException when the request is missing a
     * first name.
     */
    @Test
    public void testCreateUser_IncompleteRequest__MissingFirstName_ThrowsException() {
        CreateUserRequestDto request = TestDataFactory.createUserRequestDto();

        // set first name to null to make request incomplete
        request.setFirstName(null);

        try {
            userController.createUser(request);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("First name must be included", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the UserController createUser endpoint throws an InvalidRequestDataException when the request is missing a
     * last name.
     */
    @Test
    public void testCreateUser_IncompleteRequest__MissingLastName_ThrowsException() {
        CreateUserRequestDto request = TestDataFactory.createUserRequestDto();

        // set last name to null to make request incomplete
        request.setLastName(null);

        try {
            userController.createUser(request);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Last name must be included", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the UserController createUser endpoint throws an InvalidRequestDataException when the request is missing a
     * password.
     */
    @Test
    public void testCreateUser_IncompleteRequest__MissingPassword_ThrowsException() {
        CreateUserRequestDto request = TestDataFactory.createUserRequestDto();

        // set password to null to make request incomplete
        request.setPassword(null);

        try {
            userController.createUser(request);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Password must be included", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the UserController createUser endpoint throws an InvalidRequestDataException when the request is missing a
     * phone number.
     */
    @Test
    public void testCreateUser_IncompleteRequest__MissingPhoneNumber_ThrowsException() {
        CreateUserRequestDto request = TestDataFactory.createUserRequestDto();

        // set phone number to null to make request incomplete
        request.setPhoneNumber(null);

        try {
            userController.createUser(request);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Phone number must be included", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the UserController createUser endpoint bubbles a UserConflictException when the user already exists.
     */
    @Test
    public void testCreateUser_ConflictingUser_ThrowsException() throws Exception {
        CreateUserRequestDto request = TestDataFactory.createUserRequestDto();
        String exceptionMessage = String.format("User with username %s already exists", request.getUsername());

        // mock the service call to throw a UserConflictException
        Mockito.when(mockUserService.createUserAndProfileAndAddress(request))
            .thenThrow(new UserConflictException(exceptionMessage));

        try {
            userController.createUser(request);
            Assertions.fail("Expected UserConflictException not thrown");
        } catch (UserConflictException ex) {
            Assertions.assertEquals(exceptionMessage, ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the UserController createUser endpoint bubbles an Exception when the service call throws an Exception.
     */
    @Test
    public void testCreateUser_ServiceCallThrowsException_ThrowsException() throws Exception {
        CreateUserRequestDto request = TestDataFactory.createUserRequestDto();
        String exceptionMessage = "Service call failed";

        // mock the service call to throw an Exception
        Mockito.when(mockUserService.createUserAndProfileAndAddress(request))
            .thenThrow(new Exception(exceptionMessage));

        try {
            userController.createUser(request);
            Assertions.fail("Expected Exception not thrown");
        } catch (Exception ex) {
            Assertions.assertEquals(exceptionMessage, ex.getMessage());
        }
    }

    /**
     * Test the UserController deleteUser endpoint deletes a user when the user id is valid.
     */
    @Test
    public void testDeleteUser_ValidId_DeletesUser() throws Exception {
        String userId = TestDataFactory.TEST_USER_ID;

        // mock the user service call
        Mockito.doNothing().when(mockUserService).deleteUserByUserId(userId);

        ResponseEntity<Void> response = userController.deleteUser(userId);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(204, response.getStatusCode().value());

        // validate user service call
        Mockito.verify(mockUserService, Mockito.times(1)).deleteUserByUserId(userId);
    }

    /**
     * Test the UserController deleteUser endpoint throws an InvalidUserIdException when the user id is empty.
     */
    @Test
    public void testDeleteUser_MissingId_ThrowsInvalidUserIdException() {
        String userId = "";

        try {
            userController.deleteUser(userId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the UserController deleteUser endpoint throws an InvalidUserIdException when the user id is null.
     */
    @Test
    public void testDeleteUser_NullId_ThrowsInvalidUserIdException() {
        String userId = null;

        try {
            userController.deleteUser(userId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the UserController updateUser endpoint updates a user when the user id is valid and the request is valid.
     */
    @Test
    public void testUpdateUser_ValidRequest_UpdatesUser() throws Exception {
        PatchUserRequestDto request = TestDataFactory.createPatchUserRequestDto();
        // set the request values to something unique
        request.setFirstName("updated-" + request.getFirstName());
        request.setLastName("updated-" + request.getLastName());
        request.setPhoneNumber("updated-" + request.getPhoneNumber());
        request.setProfileImageUrl("updated-" + request.getProfileImageUrl());
        Instant updatedAt = Instant.now();

        String userId = TestDataFactory.TEST_USER_ID;

        // mock the user service call
        Mockito.when(mockUserService.updateUser(userId, request))
            .thenAnswer(invocation -> {
                PatchUserRequestDto requestDto = invocation.getArgument(1);

                UserResponseDto response = TestDataFactory.createTestUserResponseDto(TestDataFactory.TEST_USER_ID);

                // set the response values to the request values
                response.setFirstName(requestDto.getFirstName());
                response.setLastName(requestDto.getLastName());
                response.setPhoneNumber(requestDto.getPhoneNumber());
                response.setProfileImageUrl(requestDto.getProfileImageUrl());
                response.setUpdatedAt(updatedAt);

                return response;
            });

        ResponseEntity<EntityModel<UserResponseDto>> response = userController.updateUser(userId, request);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());

        // validate user and user profile
        Assertions.assertNotNull(response.getBody());
        UserResponseDto userResponse = response.getBody().getContent();
        Assertions.assertNotNull(userResponse);
        Assertions.assertEquals(userId, userResponse.getUserId());

        // check that the request values were updated
        Assertions.assertEquals(request.getFirstName(), userResponse.getFirstName());
        Assertions.assertEquals(request.getLastName(), userResponse.getLastName());
        Assertions.assertEquals(request.getPhoneNumber(), userResponse.getPhoneNumber());
        Assertions.assertEquals(request.getProfileImageUrl(), userResponse.getProfileImageUrl());

        // check the updated at timestamp was updated
        Assertions.assertEquals(updatedAt, userResponse.getUpdatedAt());
    }

    /**
     * Test the UserController updateUser endpoint only updates the request values that are set on the request.
     */
    @Test
    public void testUpdateUser_PartialRequest_UpdatesUser() throws Exception {
        String userId = TestDataFactory.TEST_USER_ID;
        PatchUserRequestDto request = TestDataFactory.createPatchUserRequestDto();
        // set some of the request values to something unique
        request.setLastName("updated-" + request.getLastName());
        request.setProfileImageUrl("updated-" + request.getProfileImageUrl());
        Instant updatedAt = Instant.now();

        // mock the user service call
        Mockito.when(mockUserService.updateUser(userId, request))
            .thenAnswer(invocation -> {
                PatchUserRequestDto requestDto = invocation.getArgument(1);

                UserResponseDto response = TestDataFactory.createTestUserResponseDto(TestDataFactory.TEST_USER_ID);

                // set the response values to the request values
                response.setLastName(requestDto.getLastName());
                response.setProfileImageUrl(requestDto.getProfileImageUrl());
                response.setUpdatedAt(updatedAt);

                return response;
            });

        ResponseEntity<EntityModel<UserResponseDto>> response = userController.updateUser(userId, request);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());

        // validate user and user profile
        Assertions.assertNotNull(response.getBody());
        UserResponseDto userResponse = response.getBody().getContent();
        Assertions.assertNotNull(userResponse);
        Assertions.assertEquals(userId, userResponse.getUserId());

        // check that the request values were updated
        Assertions.assertEquals(request.getLastName(), userResponse.getLastName());
        Assertions.assertEquals(request.getProfileImageUrl(), userResponse.getProfileImageUrl());

        // verify the unset request values were not updated
        Assertions.assertEquals(TestDataFactory.TEST_USER_USERNAME, userResponse.getUsername());
        Assertions.assertEquals(TestDataFactory.TEST_USER_EMAIL, userResponse.getEmail());
        Assertions.assertEquals(TestDataFactory.TEST_USER_FIRST_NAME, userResponse.getFirstName());
        Assertions.assertEquals(TestDataFactory.TEST_USER_PHONE_NUMBER, userResponse.getPhoneNumber());

        // check the updated at timestamp was updated
        Assertions.assertEquals(updatedAt, userResponse.getUpdatedAt());
    }

    /**
     * Test the UserController updateUser endpoint throws an InvalidUserIdException when the user id is empty.
     */
    @Test
    public void testUpdateUser_MissingId_ThrowsInvalidUserIdException() {
        String userId = "";
        PatchUserRequestDto request = TestDataFactory.createPatchUserRequestDto();

        try {
            userController.updateUser(userId, request);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the UserController updateUser endpoint throws an InvalidUserIdException when the user id is null.
     */
    @Test
    public void testUpdateUser_NullId_ThrowsInvalidUserIdException() {
        String userId = null;
        PatchUserRequestDto request = TestDataFactory.createPatchUserRequestDto();

        try {
            userController.updateUser(userId, request);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

}
