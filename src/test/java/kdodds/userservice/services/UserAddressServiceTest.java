package kdodds.userservice.services;

import kdodds.userservice.dto.requests.CreateUserAddressRequestDto;
import kdodds.userservice.dto.responses.UserAddressResponseDto;
import kdodds.userservice.dto.responses.UserAddressesResponseDto;
import kdodds.userservice.exceptions.models.exceptions.InvalidRequestDataException;
import kdodds.userservice.exceptions.models.exceptions.InvalidUserIdException;
import kdodds.userservice.exceptions.models.exceptions.UserAddressNotFound;
import kdodds.userservice.exceptions.models.exceptions.UserNotFoundException;
import kdodds.userservice.repositories.UserAddressRepository;
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

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class UserAddressServiceTest {

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private UserAddressRepository mockUserAddressRepository;

    @InjectMocks
    private UserAddressService userAddressService;

    /**
     * Setup for each test.
     */
    @BeforeEach
    public void setup() {
        // clear mocks
        Mockito.reset(
            mockUserAddressRepository,
            mockUserRepository
        );
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

        UserAddressesResponseDto response = userAddressService.getUserAddressesDtoByUserId(userId);

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
            userAddressService.getUserAddressesDtoByUserId(userId);
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
            userAddressService.getUserAddressesDtoByUserId(userId);
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
            userAddressService.getUserAddressesDtoByUserId(userId);
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

        UserAddressesResponseDto response = userAddressService.getUserAddressesDtoByUserId(userId);

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

        UserAddressResponseDto response = userAddressService.getUserAddressDtoById(userId, addressId);

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
            userAddressService.getUserAddressDtoById(userId, addressId);
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
            userAddressService.getUserAddressDtoById(userId, addressId);
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
            userAddressService.getUserAddressDtoById(userId, addressId);
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
            userAddressService.getUserAddressDtoById(userId, addressId);
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
            userAddressService.getUserAddressDtoById(userId, addressId);
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
            userAddressService.getUserAddressDtoById(userId, addressId);
            Assertions.fail("Expected UserAddressNotFound not thrown");
        } catch (UserAddressNotFound ex) {
            Assertions.assertEquals(
                String.format(
                    "No user address found for userId %s and addressId %s",
                    userId,
                    addressId
                ), ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the createUserAddress method creates a valid address when given a valid request and userId.
     */
    @Test
    public void testCreateUserAddress_ValidRequest_CreatesAddress() {
        CreateUserAddressRequestDto request = TestDataFactory.createCreateUserAddressRequestDto();

        // mock the repository response (user reference)
        Mockito.when(mockUserRepository.getReferenceById(UUID.fromString(TestDataFactory.TEST_USER_ID))).thenReturn(
            TestDataFactory.createTestUserEntity(TestDataFactory.TEST_USER_ID, true)
        );

        // mock the repository response (saveAndFlush)
        Mockito.when(mockUserAddressRepository.saveAndFlush(any()))
            .thenReturn(TestDataFactory.createTestUserAddressEntity(
                TestDataFactory.TEST_USER_ID
            ));

        // mock the repository response (findById)
        Mockito.when(mockUserAddressRepository.findById(any())).thenReturn(
            Optional.of(TestDataFactory.createTestUserAddressEntity(TestDataFactory.TEST_USER_ID))
        );

        try {
            UserAddressResponseDto response = userAddressService.createUserAddress(
                TestDataFactory.TEST_USER_ID,
                request
            );

            // validate response
            Assertions.assertNotNull(response);
            Assertions.assertEquals(TestDataFactory.TEST_USER_ID, response.getUserId());
            Assertions.assertEquals(TestDataFactory.TEST_ADDRESS_ID_1, response.getAddressId());
            Assertions.assertEquals(TestDataFactory.TEST_USER_ADDRESS_LINE_1, response.getAddressLine1());
            Assertions.assertEquals(TestDataFactory.TEST_USER_ADDRESS_TYPE, response.getAddressType());
            Assertions.assertEquals(TestDataFactory.TEST_USER_CITY, response.getCity());
            Assertions.assertEquals(TestDataFactory.TEST_USER_STATE, response.getState());
            Assertions.assertEquals(TestDataFactory.TEST_USER_ZIP_CODE, response.getZipCode());
            Assertions.assertEquals(TestDataFactory.TEST_USER_COUNTRY, response.getCountry());
            Assertions.assertNotNull(response.getCreatedAt());
            Assertions.assertNotNull(response.getUpdatedAt());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }

    /**
     * Test the createUserAddress method throws an exception when the userId is null.
     */
    @Test
    public void testCreateUserAddress_NullUserId_ThrowsInvalidUserIdException() {
        CreateUserAddressRequestDto request = TestDataFactory.createCreateUserAddressRequestDto();

        try {
            userAddressService.createUserAddress(null, request);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the createUserAddress method throws an exception when the userId is empty.
     */
    @Test
    public void testCreateUserAddress_EmptyUserId_ThrowsInvalidUserIdException() {
        CreateUserAddressRequestDto request = TestDataFactory.createCreateUserAddressRequestDto();

        try {
            userAddressService.createUserAddress("", request);
            Assertions.fail("Expected InvalidUserIdException not thrown");
        } catch (InvalidUserIdException ex) {
            Assertions.assertEquals("Invalid null or empty user id", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the createUserAddress method throws an exception when the request is null.
     */
    @Test
    public void testCreateUserAddress_NullRequest_ThrowsInvalidRequestDataException() {
        try {
            userAddressService.createUserAddress(TestDataFactory.TEST_USER_ID, null);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Request body must be included in Create User Address request", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the createUserAddress method throws an exception when the request has a null address line 1.
     */
    @Test
    public void testCreateUserAddress_RequestNullAddressLine1_ThrowsInvalidRequestDataException() {
        CreateUserAddressRequestDto request = TestDataFactory.createCreateUserAddressRequestDto();
        request.setAddressLine1(null);

        try {
            userAddressService.createUserAddress(TestDataFactory.TEST_USER_ID, request);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Address line 1 must be included in create user address request", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the createUserAddress method throws an exception when the request has an empty address line 1.
     */
    @Test
    public void testCreateUserAddress_RequestEmptyAddressLine1_ThrowsInvalidRequestDataException() {
        CreateUserAddressRequestDto request = TestDataFactory.createCreateUserAddressRequestDto();
        request.setAddressLine1("");

        try {
            userAddressService.createUserAddress(TestDataFactory.TEST_USER_ID, request);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Address line 1 must be included in create user address request", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the createUserAddress method throws an exception when the request has a null city.
     */
    @Test
    public void testCreateUserAddress_RequestNullCity_ThrowsInvalidRequestDataException() {
        CreateUserAddressRequestDto request = TestDataFactory.createCreateUserAddressRequestDto();
        request.setCity(null);

        try {
            userAddressService.createUserAddress(TestDataFactory.TEST_USER_ID, request);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("City must be included in create user address request", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the createUserAddress method throws an exception when the request has an empty city.
     */
    @Test
    public void testCreateUserAddress_RequestEmptyAddressCity_ThrowsInvalidRequestDataException() {
        CreateUserAddressRequestDto request = TestDataFactory.createCreateUserAddressRequestDto();
        request.setCity("");

        try {
            userAddressService.createUserAddress(TestDataFactory.TEST_USER_ID, request);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("City must be included in create user address request", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the createUserAddress method throws an exception when the request has a null state.
     */
    @Test
    public void testCreateUserAddress_RequestNullState_ThrowsInvalidRequestDataException() {
        CreateUserAddressRequestDto request = TestDataFactory.createCreateUserAddressRequestDto();
        request.setState(null);

        try {
            userAddressService.createUserAddress(TestDataFactory.TEST_USER_ID, request);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("State must be included in create user address request", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the createUserAddress method throws an exception when the request has an empty state.
     */
    @Test
    public void testCreateUserAddress_RequestEmptyAddressState_ThrowsInvalidRequestDataException() {
        CreateUserAddressRequestDto request = TestDataFactory.createCreateUserAddressRequestDto();
        request.setState("");

        try {
            userAddressService.createUserAddress(TestDataFactory.TEST_USER_ID, request);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("State must be included in create user address request", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the createUserAddress method throws an exception when the request has a null zip code.
     */
    @Test
    public void testCreateUserAddress_RequestNullZipCode_ThrowsInvalidRequestDataException() {
        CreateUserAddressRequestDto request = TestDataFactory.createCreateUserAddressRequestDto();
        request.setZipCode(null);

        try {
            userAddressService.createUserAddress(TestDataFactory.TEST_USER_ID, request);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Zip code must be included in create user address request", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the createUserAddress method throws an exception when the request has an empty zip code.
     */
    @Test
    public void testCreateUserAddress_RequestEmptyAddressZipCode_ThrowsInvalidRequestDataException() {
        CreateUserAddressRequestDto request = TestDataFactory.createCreateUserAddressRequestDto();
        request.setZipCode("");

        try {
            userAddressService.createUserAddress(TestDataFactory.TEST_USER_ID, request);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Zip code must be included in create user address request", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the createUserAddress method throws an exception when the request has a null country.
     */
    @Test
    public void testCreateUserAddress_RequestNullCountry_ThrowsInvalidRequestDataException() {
        CreateUserAddressRequestDto request = TestDataFactory.createCreateUserAddressRequestDto();
        request.setCountry(null);

        try {
            userAddressService.createUserAddress(TestDataFactory.TEST_USER_ID, request);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Country must be included in create user address request", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the createUserAddress method throws an exception when the request has an empty country.
     */
    @Test
    public void testCreateUserAddress_RequestEmptyCountry_ThrowsInvalidRequestDataException() {
        CreateUserAddressRequestDto request = TestDataFactory.createCreateUserAddressRequestDto();
        request.setCountry("");

        try {
            userAddressService.createUserAddress(TestDataFactory.TEST_USER_ID, request);
            Assertions.fail("Expected InvalidRequestDataException not thrown");
        } catch (InvalidRequestDataException ex) {
            Assertions.assertEquals("Country must be included in create user address request", ex.getMessage());
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the createUserAddress method throws an exception when the user does not exist.
     */
    @Test
    public void testCreateUserAddress_UserDoesNotExist_ThrowsUserNotFoundException() {
        CreateUserAddressRequestDto request = TestDataFactory.createCreateUserAddressRequestDto();

        // mock the user repository response
        Mockito.when(mockUserRepository.getReferenceById(UUID.fromString(TestDataFactory.TEST_USER_ID)))
            .thenReturn(null);

        // mock the user address repo saveAndFlush to throw a DataIntegrityViolationException w/ a
        // ConstraintViolationException
        Mockito.when(mockUserAddressRepository.saveAndFlush(any()))
            .thenThrow(
                new DataIntegrityViolationException(
                    "Key constraint",
                    new org.hibernate.exception.ConstraintViolationException(
                        "user_addresses_user_id_fkey does not exist",
                        new SQLException(),
                        ""
                    )
                )
            );

        try {
            userAddressService.createUserAddress(TestDataFactory.TEST_USER_ID, request);
            Assertions.fail("Expected UserNotFoundException not thrown");
        } catch (UserNotFoundException ex) {
            Assertions.assertEquals(
                String.format("User with id %s not found", TestDataFactory.TEST_USER_ID),
                ex.getMessage()
            );
        } catch (Exception ex) {
            Assertions.fail("Unexpected exception type thrown: " + ex.getClass().getName());
        }
    }

    /**
     * Test the createUserAddress method throws an exception when the call to the repository throws an exception.
     */
    @Test
    public void testCreateUserAddress_RepositoryThrowsException_ThrowsUserAddressException() {
        CreateUserAddressRequestDto request = TestDataFactory.createCreateUserAddressRequestDto();

        // mock the user repository response
        Mockito.when(mockUserRepository.getReferenceById(UUID.fromString(TestDataFactory.TEST_USER_ID)))
            .thenReturn(TestDataFactory.createTestUserEntity(TestDataFactory.TEST_USER_ID, true));

        // mock the user address repo response
        Mockito.when(mockUserAddressRepository.saveAndFlush(any()))
            .thenThrow(new RuntimeException("mock exception"));

        try {
            userAddressService.createUserAddress(TestDataFactory.TEST_USER_ID, request);
            Assertions.fail("Expected Exception not thrown");
        } catch (Exception ex) {
            Assertions.assertEquals(
                String.format("Error creating user address for user id: %s", TestDataFactory.TEST_USER_ID),
                ex.getMessage()
            );
        }
    }

}
