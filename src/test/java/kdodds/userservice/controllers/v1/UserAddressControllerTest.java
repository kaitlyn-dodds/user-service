package kdodds.userservice.controllers.v1;

import kdodds.userservice.assemblers.UserAddressModelAssembler;
import kdodds.userservice.assemblers.UserAddressesModelAssembler;
import kdodds.userservice.dto.requests.CreateUserAddressRequestDto;
import kdodds.userservice.dto.responses.UserAddressResponseDto;
import kdodds.userservice.dto.responses.UserAddressesResponseDto;
import kdodds.userservice.exceptions.models.exceptions.InvalidRequestDataException;
import kdodds.userservice.exceptions.models.exceptions.InvalidUserIdException;
import kdodds.userservice.services.UserAddressService;
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

import java.util.List;

@SpringBootTest
public class UserAddressControllerTest {

    @Mock
    private UserAddressModelAssembler mockUserAddressModelAssembler;

    @Mock
    private UserAddressesModelAssembler mockUserAddressesModelAssembler;

    @Mock
    private UserService mockUserService;

    @Mock
    private UserAddressService mockUserAddressService;

    @InjectMocks
    private UserAddressController userAddressController;

    /**
     * Setup for each test.
     */
    @BeforeEach
    public void setup() {
        // clear mocks
        Mockito.reset(
            mockUserAddressModelAssembler,
            mockUserAddressesModelAssembler,
            mockUserService,
            mockUserAddressService
        );

        // mock the user address model assembler to just return the input wrapped in an EntityModel
        Mockito.when(mockUserAddressModelAssembler.toModel(Mockito.any(UserAddressResponseDto.class)))
            .thenAnswer(invocation -> {
                UserAddressResponseDto argUserAddressDto = invocation.getArgument(0);
                return EntityModel.of(argUserAddressDto);
            });

        // mock the user addresses model assembler to just return the input wrapped in an EntityModel
        Mockito.when(mockUserAddressesModelAssembler.toModel(Mockito.any(UserAddressesResponseDto.class)))
            .thenAnswer(invocation -> {
                UserAddressesResponseDto argUserAddressesDto = invocation.getArgument(0);
                return EntityModel.of(argUserAddressesDto);
            });

    }

    /**
     * Test the userAddressController /users/{userId}/addresses endpoint returns a 200 status code along w/ a list of
     * UserAddresses.
     */
    @Test
    public void testGetUserAddressesById_ValidId_ReturnsUserAddresses() throws Exception {
        String userId = TestDataFactory.TEST_USER_ID;

        // mock service call
        Mockito.when(mockUserAddressService.getUserAddressesDtoByUserId(userId)).thenReturn(
            UserAddressesResponseDto.builder()
                .userId(userId)
                .addresses(List.of(TestDataFactory.createTestUserAddressDto(userId)))
                .build()
        );

        ResponseEntity<EntityModel<UserAddressesResponseDto>> response
            = userAddressController.getUserAddressesByUserId(userId);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());

        // validate addresses
        Assertions.assertNotNull(response.getBody());
        UserAddressesResponseDto userAddressesResponse = response.getBody().getContent();
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
     * Test the userAddressController /users/{userId}/addresses endpoint throws an InvalidUserIdException when the user
     * id is empty.
     */
    @Test
    public void testGetUserAddressesById_MissingId_ThrowsInvalidUserIdException() {
        String userId = "";

        try {
            userAddressController.getUserAddressesByUserId(userId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the userAddressController /users/{userId}/addresses endpoint throws an InvalidUserIdException when the user
     * id is null.
     */
    @Test
    public void testGetUserAddressesById_NullId_ThrowsInvalidUserIdException() {
        String userId = null;

        try {
            userAddressController.getUserAddressesByUserId(userId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the userAddressController /users/{userId}/addresses/{addressId} endpoint returns a 200 status code along w/
     * a complete UserAddress.
     */
    @Test
    public void testGetUserAddressById_ValidIds_ReturnExpectedAddress() throws Exception {
        String userId = TestDataFactory.TEST_USER_ID;
        String addressId = TestDataFactory.TEST_ADDRESS_ID_1;

        // mock user service response
        Mockito.when(mockUserAddressService.getUserAddressDtoById(userId, addressId)).thenReturn(
            TestDataFactory.createTestUserAddressDto(userId)
        );

        ResponseEntity<EntityModel<UserAddressResponseDto>>
            response = userAddressController.getUserAddressById(userId, addressId);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());

        // validate address
        Assertions.assertNotNull(response.getBody());
        UserAddressResponseDto userAddressResponse = response.getBody().getContent();
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
     * Test the userAddressController /users/{userId}/addresses/{addressId} endpoint throws an InvalidUserIdException
     * when the user id is empty.
     */
    @Test
    public void testGetUserAddressById_MissingUserId_ThrowsInvalidUserIdException() {
        String userId = "";
        String addressId = TestDataFactory.TEST_ADDRESS_ID_1;

        try {
            userAddressController.getUserAddressById(userId, addressId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the userAddressController /users/{userId}/addresses/{addressId} endpoint throws an InvalidUserIdException
     * when the user id is null.
     */
    @Test
    public void testGetUserAddressById_NullUserId_ThrowsInvalidUserIdException() {
        String userId = null;
        String addressId = TestDataFactory.TEST_ADDRESS_ID_1;

        try {
            userAddressController.getUserAddressById(userId, addressId);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the userAddressController /users/{userId}/addresses/{addressId} endpoint throws an InvalidRequestData
     * exception when the address id is empty.
     */
    @Test
    public void testGetUserAddressById_MissingAddressId_ThrowsInvalidRequestData() {
        String userId = TestDataFactory.TEST_USER_ID;
        String addressId = "";

        try {
            userAddressController.getUserAddressById(userId, addressId);
            Assertions.fail("Expected InvalidRequestData exception not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Invalid null or empty address id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the userAddressController /users/{userId}/addresses/{addressId} endpoint throws an InvalidRequestData
     * exception when the address id is null.
     */
    @Test
    public void testGetUserAddressById_NullAddressId_ThrowsInvalidRequestData() {
        String userId = TestDataFactory.TEST_USER_ID;
        String addressId = null;

        try {
            userAddressController.getUserAddressById(userId, addressId);
            Assertions.fail("Expected InvalidRequestData exception not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Invalid null or empty address id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the POST /users/{userId}/addresses createUserAddressesForUserId endpoint creates a new user address for the
     * given user when provided with a valid CreateUserAddressRequestDto.
     */
    @Test
    public void testCreateUserAddressesForUserId_ValidRequest_CreateAddress() throws Exception {
        CreateUserAddressRequestDto request = TestDataFactory.createCreateUserAddressRequestDto();

        // mock service call
        Mockito.when(mockUserAddressService.createUserAddress(TestDataFactory.TEST_USER_ID, request))
            .thenReturn(TestDataFactory.createTestUserAddressDto(TestDataFactory.TEST_USER_ID));

        ResponseEntity<EntityModel<UserAddressResponseDto>> response = userAddressController
            .createUserAddressesForUserId(TestDataFactory.TEST_USER_ID, request);

        // validate response
        Assertions.assertNotNull(response);
        Assertions.assertEquals(201, response.getStatusCode().value());

        // validate user address
        Assertions.assertNotNull(response.getBody());
        UserAddressResponseDto userAddressResponse = response.getBody().getContent();
        Assertions.assertNotNull(userAddressResponse);
        Assertions.assertNotNull(userAddressResponse.getAddressId());
        Assertions.assertEquals(TestDataFactory.TEST_USER_ID, userAddressResponse.getUserId());
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
     * Test the POST /users/{userId}/addresses createUserAddressesForUserId endpoint throws an
     * InvalidRequestDataException when the userId is null.
     */
    @Test
    public void testCreateUserAddressesForUserId_NullUserId_ThrowsInvalidRequestDataException() {
        CreateUserAddressRequestDto request = TestDataFactory.createCreateUserAddressRequestDto();

        try {
            userAddressController.createUserAddressesForUserId(null, request);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Request body and user id must be included", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the POST /users/{userId}/addresses createUserAddressesForUserId endpoint throws an
     * InvalidRequestDataException when the userId is empty.
     */
    @Test
    public void testCreateUserAddressesForUserId_EmptyUserId_ThrowsInvalidRequestDataException() {
        CreateUserAddressRequestDto request = TestDataFactory.createCreateUserAddressRequestDto();

        try {
            userAddressController.createUserAddressesForUserId("", request);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Request body and user id must be included", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the POST /users/{userId}/addresses createUserAddressesForUserId endpoint throws an
     * InvalidRequestDataException when the request body is null.
     */
    @Test
    public void testCreateUserAddressesForUserId_NullRequest_ThrowsInvalidRequestDataException() {
        try {
            userAddressController.createUserAddressesForUserId(TestDataFactory.TEST_USER_ID, null);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Request body and user id must be included", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

}
