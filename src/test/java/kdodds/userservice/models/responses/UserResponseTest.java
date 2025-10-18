package kdodds.userservice.models.responses;

import com.fasterxml.jackson.databind.ObjectMapper;
import kdodds.userservice.models.CompleteUserData;
import kdodds.userservice.utils.TestDataFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static kdodds.userservice.utils.TestDataFactory.convertUserAddressesToResponse;

@SpringBootTest
public class UserResponseTest {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test serialization of UserResponse.
     */
    @Test
    public void testSerialization() throws Exception {
        CompleteUserData completeUserData = TestDataFactory.createTestCompleteUserData(
            TestDataFactory.TEST_USER_ID
        );

        UserResponse response = UserResponse.builder()
            .userId(completeUserData.getUser().getId())
            .username(completeUserData.getUser().getUsername())
            .email(completeUserData.getUser().getEmail())
            .createdAt(completeUserData.getUser().getCreatedAt())
            .updatedAt(completeUserData.getUser().getUpdatedAt())
            .firstName(completeUserData.getUserProfile().getFirstName())
            .lastName(completeUserData.getUserProfile().getLastName())
            .phoneNumber(completeUserData.getUserProfile().getPhoneNumber())
            .profileImageUrl(completeUserData.getUserProfile().getProfileImageUrl())
            .addresses(convertUserAddressesToResponse(completeUserData.getUserAddresses()))
            .build();

        String expectedJson = "{"
            + "\"user_id\":\"" + TestDataFactory.TEST_USER_ID + "\","
            + "\"username\":\"" + TestDataFactory.TEST_USER_USERNAME + "\","
            + "\"email\":\"" + TestDataFactory.TEST_USER_EMAIL + "\","
            + "\"first_name\":\"" + TestDataFactory.TEST_USER_FIRST_NAME + "\","
            + "\"last_name\":\"" + TestDataFactory.TEST_USER_LAST_NAME + "\","
            + "\"phone_number\":\"" + TestDataFactory.TEST_USER_PHONE_NUMBER + "\","
            + "\"profile_image_url\":\"" + TestDataFactory.TEST_USER_PROFILE_IMAGE_URL + "\","
            + "\"addresses\":["
            + "{"
            + "\"id\":\"" + completeUserData.getUserAddresses().getFirst().getId() + "\","
            + "\"user_id\":\"" + TestDataFactory.TEST_USER_ID + "\","
            + "\"address_type\":\"" + TestDataFactory.TEST_USER_ADDRESS_TYPE + "\","
            + "\"address_line_1\":\"" + TestDataFactory.TEST_USER_ADDRESS_LINE_1 + "\","
            + "\"city\":\"" + TestDataFactory.TEST_USER_CITY + "\","
            + "\"state\":\"" + TestDataFactory.TEST_USER_STATE + "\","
            + "\"zip_code\":\"" + TestDataFactory.TEST_USER_ZIP_CODE + "\","
            + "\"country\":\"" + TestDataFactory.TEST_USER_COUNTRY + "\","
            + "\"created_at\":\"" + completeUserData.getUserAddresses().getFirst().getCreatedAt().toString() + "\","
            + "\"updated_at\":\"" + completeUserData.getUserAddresses().getFirst().getUpdatedAt().toString() + "\""
            + "}],"
            + "\"created_at\":\"" + completeUserData.getUser().getCreatedAt().toString() + "\","
            + "\"updated_at\":\"" + completeUserData.getUser().getUpdatedAt().toString() + "\""
            + "}";

        Assertions.assertEquals(expectedJson, objectMapper.writeValueAsString(response));
    }

    /**
     * Test deserialization of UserResponse.
     */
    @Test
    public void testDeserialization() throws Exception {
        String jsonString = "{"
            + "\"user_id\":\"" + TestDataFactory.TEST_USER_ID + "\","
            + "\"username\":\"" + TestDataFactory.TEST_USER_USERNAME + "\","
            + "\"email\":\"" + TestDataFactory.TEST_USER_EMAIL + "\","
            + "\"first_name\":\"" + TestDataFactory.TEST_USER_FIRST_NAME + "\","
            + "\"last_name\":\"" + TestDataFactory.TEST_USER_LAST_NAME + "\","
            + "\"phone_number\":\"" + TestDataFactory.TEST_USER_PHONE_NUMBER + "\","
            + "\"profile_image_url\":\"" + TestDataFactory.TEST_USER_PROFILE_IMAGE_URL + "\","
            + "\"addresses\":["
            + "{"
            + "\"id\":\"addr-12345\","
            + "\"user_id\":\"" + TestDataFactory.TEST_USER_ID + "\","
            + "\"address_type\":\"" + TestDataFactory.TEST_USER_ADDRESS_TYPE + "\","
            + "\"address_line_1\":\"" + TestDataFactory.TEST_USER_ADDRESS_LINE_1 + "\","
            + "\"city\":\"" + TestDataFactory.TEST_USER_CITY + "\","
            + "\"state\":\"" + TestDataFactory.TEST_USER_STATE + "\","
            + "\"zip_code\":\"" + TestDataFactory.TEST_USER_ZIP_CODE + "\","
            + "\"country\":\"" + TestDataFactory.TEST_USER_COUNTRY + "\","
            + "\"created_at\":\"2024-01-01T12:00:00Z\","
            + "\"updated_at\":\"2024-01-02T12:00:00Z\""
            + "}],"
            + "\"created_at\":\"2024-01-01T12:00:00Z\","
            + "\"updated_at\":\"2024-01-02T12:00:00Z\""
            + "}";

        UserResponse response = objectMapper.readValue(jsonString, UserResponse.class);

        Assertions.assertEquals(TestDataFactory.TEST_USER_ID, response.getUserId());
        Assertions.assertEquals(TestDataFactory.TEST_USER_USERNAME, response.getUsername());
        Assertions.assertEquals(TestDataFactory.TEST_USER_EMAIL, response.getEmail());
        Assertions.assertEquals(TestDataFactory.TEST_USER_FIRST_NAME, response.getFirstName());
        Assertions.assertEquals(TestDataFactory.TEST_USER_LAST_NAME, response.getLastName());
        Assertions.assertEquals(TestDataFactory.TEST_USER_PHONE_NUMBER, response.getPhoneNumber());
        Assertions.assertEquals(TestDataFactory.TEST_USER_PROFILE_IMAGE_URL, response.getProfileImageUrl());
        Assertions.assertEquals(1, response.getAddresses().size());
        Assertions.assertEquals("addr-12345", response.getAddresses().getFirst().getId());
        Assertions.assertEquals("2024-01-01T12:00:00Z", response.getCreatedAt().toString());
        Assertions.assertEquals("2024-01-02T12:00:00Z", response.getUpdatedAt().toString());
    }

}
