package kdodds.userservice.utils;

import kdodds.userservice.dto.responses.UserAddressResponseDto;
import kdodds.userservice.dto.responses.UserAddressesResponseDto;
import kdodds.userservice.dto.responses.UserProfileResponseDto;
import kdodds.userservice.dto.responses.UserResponseDto;
import kdodds.userservice.entities.User;
import kdodds.userservice.entities.UserAddress;
import kdodds.userservice.entities.UserProfile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class TestDataFactory {

    public static final String TEST_USER_ID = "937758e0-abe0-4dd4-827d-b868169bc160";

    public static final String TEST_USER_EMAIL = "somewhere@someplace.com";

    public static final String TEST_USER_USERNAME = "magicalwizardman4848";

    public static final String TEST_USER_PASSWORD = "password";

    public static final String TEST_USER_PHONE_NUMBER = "5746857273733";

    public static final String TEST_USER_PROFILE_IMAGE_URL = "www.someurl.com";

    public static final String TEST_USER_FIRST_NAME = "Tom";

    public static final String TEST_USER_LAST_NAME = "Bombadil";

    public static final String TEST_USER_ADDRESS_LINE_1 = "1717 Old Forest Rd";

    public static final String TEST_USER_ADDRESS_TYPE = "Home";

    public static final String TEST_USER_CITY = "Old Forest";

    public static final String TEST_USER_STATE = "Old Forest";

    public static final String TEST_USER_ZIP_CODE = "17171";

    public static final String TEST_USER_COUNTRY = "USA";

    public static final String TEST_ADDRESS_ID_1 = "12345678-1234-1234-1234-123456789012";

    /**
     * Generates a random UUID.
     *
     * @return String UUID.
     */
    public static String generateRandomUuid() {
        return java.util.UUID.randomUUID().toString();
    }


    /**
     * Creates a test CompleteUserData object.
     *
     * @return CompleteUserData object.
     */
    public static UserResponseDto createTestUserResponseDto() {
        String userId = TestDataFactory.generateRandomUuid();

        return TestDataFactory.createTestUserResponseDto(userId);
    }

    /**
     * Creates a test CompleteUserData object for a given user id.
     *
     * @param userId The user id to use for the test data.
     * @return CompleteUserData object.
     */
    public static UserResponseDto createTestUserResponseDto(String userId) {
        return UserResponseDto.builder()
            .userId(userId)
            .username(TestDataFactory.TEST_USER_USERNAME)
            .email(TestDataFactory.TEST_USER_EMAIL)
            .firstName(TestDataFactory.TEST_USER_FIRST_NAME)
            .lastName(TestDataFactory.TEST_USER_LAST_NAME)
            .phoneNumber(TestDataFactory.TEST_USER_PHONE_NUMBER)
            .profileImageUrl(TestDataFactory.TEST_USER_PROFILE_IMAGE_URL)
            .addresses(List.of(
                TestDataFactory.createTestUserAddressDto(userId)
            ))
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }

    /**
     * Creates a test UserProfile object.
     *
     * @param userId The user id to use for the test data.
     * @return UserProfile object.
     */
    public static UserProfileResponseDto createTestUserProfileDto(String userId) {
        return UserProfileResponseDto.builder()
            .userId(userId)
            .firstName(TestDataFactory.TEST_USER_FIRST_NAME)
            .lastName(TestDataFactory.TEST_USER_LAST_NAME)
            .phoneNumber(TestDataFactory.TEST_USER_PHONE_NUMBER)
            .profileImageUrl(TestDataFactory.TEST_USER_PROFILE_IMAGE_URL)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }

    /**
     * Creates a test UserAddressesResponseDto object.
     *
     * @return UserAddressesResponseDto object.
     */
    public static UserAddressesResponseDto createTestUserAddressesDto() {
        return UserAddressesResponseDto.builder()
            .userId(TestDataFactory.TEST_USER_ID)
            .addresses(List.of(TestDataFactory.createTestUserAddressDto(TestDataFactory.TEST_USER_ID)))
            .build();
    }

    /**
     * Creates a test UserAddressResponseDto object.
     *
     * @param userId The user id to use for the test data.
     * @return UserAddressResponseDto object.
     */
    public static UserAddressResponseDto createTestUserAddressDto(String userId) {
        return UserAddressResponseDto.builder()
            .id(TestDataFactory.TEST_ADDRESS_ID_1)
            .userId(userId)
            .addressLine1(TestDataFactory.TEST_USER_ADDRESS_LINE_1)
            .addressType(TestDataFactory.TEST_USER_ADDRESS_TYPE)
            .city(TestDataFactory.TEST_USER_CITY)
            .state(TestDataFactory.TEST_USER_STATE)
            .zipCode(TestDataFactory.TEST_USER_ZIP_CODE)
            .country(TestDataFactory.TEST_USER_COUNTRY)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }

    /**
     * Creates a test User entity.
     */
    public static User createTestUserEntity(String userId) {
        UserProfile profile = TestDataFactory.createTestUserProfileEntity(userId);

        List<UserAddress> addresses = List.of(
            TestDataFactory.createTestUserAddressEntity(userId)
        );

        // User
        User user = new User();
        user.setId(UUID.fromString(userId));
        user.setUsername(TestDataFactory.TEST_USER_USERNAME);
        user.setEmail(TestDataFactory.TEST_USER_EMAIL);
        user.setPasswordHash(TestDataFactory.TEST_USER_PASSWORD);
        user.setStatus("ACTIVE");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        user.setUserProfile(profile);
        user.setAddresses(addresses);

        // set user on addresses (necessary for mapper functions)
        addresses.forEach(address -> address.setUser(user));

        return user;
    }

    /**
     * Create a test UserProfile.
     */
    public static UserProfile createTestUserProfileEntity(String userId) {
        UserProfile profile = new UserProfile();

        profile.setUserId(UUID.fromString(userId));
        profile.setFirstName(TestDataFactory.TEST_USER_FIRST_NAME);
        profile.setLastName(TestDataFactory.TEST_USER_LAST_NAME);
        profile.setPhoneNumber(TestDataFactory.TEST_USER_PHONE_NUMBER);
        profile.setProfileImageUrl(TestDataFactory.TEST_USER_PROFILE_IMAGE_URL);
        profile.setCreatedAt(Instant.now());
        profile.setUpdatedAt(Instant.now());

        return profile;
    }

    /**
     * Create a test UserAddress.
     */
    public static UserAddress createTestUserAddressEntity(String userId) {
        UserAddress address = new UserAddress();

        address.setId(UUID.fromString(TestDataFactory.TEST_ADDRESS_ID_1));
        address.setAddressType(TestDataFactory.TEST_USER_ADDRESS_TYPE);
        address.setAddressLine1(TestDataFactory.TEST_USER_ADDRESS_LINE_1);
        address.setCity(TestDataFactory.TEST_USER_CITY);
        address.setState(TestDataFactory.TEST_USER_STATE);
        address.setZipCode(TestDataFactory.TEST_USER_ZIP_CODE);
        address.setCountry(TestDataFactory.TEST_USER_COUNTRY);
        address.setCreatedAt(Instant.now());
        address.setUpdatedAt(Instant.now());

        return address;
    }

}
