package kdodds.userservice.utils;

import kdodds.userservice.models.CompleteUserData;
import kdodds.userservice.models.User;
import kdodds.userservice.models.UserAddress;
import kdodds.userservice.models.UserProfile;
import kdodds.userservice.models.responses.UserAddressResponse;

import java.time.Instant;
import java.util.List;

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
    public static CompleteUserData createTestCompleteUserData() {
        String userId = TestDataFactory.generateRandomUuid();

        return TestDataFactory.createTestCompleteUserData(userId);
    }

    /**
     * Creates a test CompleteUserData object for a given user id.
     *
     * @param userId The user id to use for the test data.
     * @return CompleteUserData object.
     */
    public static CompleteUserData createTestCompleteUserData(String userId) {
        return CompleteUserData.builder()
            .user(TestDataFactory.createTestUser(userId))
            .userProfile(TestDataFactory.createTestUserProfile(userId))
            .userAddresses(
                List.of(
                    TestDataFactory.createTestUserAddress(userId)
                )
            )
            .build();
    }

    /**
     * Creates a test User object.
     *
     * @param userId The user id to use for the test data.
     * @return User object.
     */
    public static User createTestUser(String userId) {
        return User.builder()
            .id(userId)
            .email(TestDataFactory.TEST_USER_EMAIL)
            .passwordHash(TestDataFactory.TEST_USER_PASSWORD)
            .status("active")
            .username(TestDataFactory.TEST_USER_USERNAME)
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
    public static UserProfile createTestUserProfile(String userId) {
        return UserProfile.builder()
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
     * Creates a test UserAddress object.
     *
     * @param userId The user id to use for the test data.
     * @return UserAddress object.
     */
    public static UserAddress createTestUserAddress(String userId) {
        return UserAddress.builder()
            .id(generateRandomUuid())
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
     * Converts a list of UserAddress objects to a list of UserAddressResponse objects.
     *
     * @param addresses List of UserAddress objects.
     * @return List of UserAddressResponse objects.
     */
    public static List<UserAddressResponse> convertUserAddressesToResponse(List<UserAddress> addresses) {
        return addresses.stream()
            .map(UserAddress::convert)
            .toList();
    }

}
