package kdodds.user_service.models.responses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kdodds.user_service.models.UserProfile;
import kdodds.user_service.utils.TestDataFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserProfileResponseTest {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test serialization of UserProfileResponse.
     */
    @Test
    public void testSerialization() throws JsonProcessingException {
        UserProfile profile = TestDataFactory.createTestUserProfile(TestDataFactory.TEST_USER_ID);
        UserProfileResponse response = UserProfileResponse.builder()
            .userId(profile.getUserId())
            .firstName(profile.getFirstName())
            .lastName(profile.getLastName())
            .phoneNumber(profile.getPhoneNumber())
            .profileImageUrl(profile.getProfileImageUrl())
            .createdAt(profile.getCreatedAt())
            .updatedAt(profile.getUpdatedAt())
            .build();

        // expected json
        String expected = "{"
            + "\"user_id\":\"" + TestDataFactory.TEST_USER_ID + "\","
            + "\"first_name\":\"" + TestDataFactory.TEST_USER_FIRST_NAME + "\","
            + "\"last_name\":\"" + TestDataFactory.TEST_USER_LAST_NAME + "\","
            + "\"phone_number\":\"" + TestDataFactory.TEST_USER_PHONE_NUMBER + "\","
            + "\"profile_image_url\":\"" + TestDataFactory.TEST_USER_PROFILE_IMAGE_URL + "\","
            + "\"created_at\":\"" + profile.getCreatedAt().toString() + "\","
            + "\"updated_at\":\"" + profile.getUpdatedAt().toString() + "\""
            + "}";

        Assertions.assertEquals(expected, objectMapper.writeValueAsString(response));
    }

    /**
     * Test deserialization of UserProfileResponse.
     */
    @Test
    public void testDeserialization() throws JsonProcessingException {
        String jsonString = "{"
            + "\"user_id\":\"" + TestDataFactory.TEST_USER_ID + "\","
            + "\"first_name\":\"" + TestDataFactory.TEST_USER_FIRST_NAME + "\","
            + "\"last_name\":\"" + TestDataFactory.TEST_USER_LAST_NAME + "\","
            + "\"phone_number\":\"" + TestDataFactory.TEST_USER_PHONE_NUMBER + "\","
            + "\"profile_image_url\":\"" + TestDataFactory.TEST_USER_PROFILE_IMAGE_URL + "\","
            + "\"created_at\":\"2024-01-01T12:00:00Z\","
            + "\"updated_at\":\"2024-01-02T12:00:00Z\""
            + "}";

        UserProfileResponse response = objectMapper.readValue(jsonString, UserProfileResponse.class);

        Assertions.assertEquals(TestDataFactory.TEST_USER_ID, response.getUserId());
        Assertions.assertEquals(TestDataFactory.TEST_USER_FIRST_NAME, response.getFirstName());
        Assertions.assertEquals(TestDataFactory.TEST_USER_LAST_NAME, response.getLastName());
        Assertions.assertEquals(TestDataFactory.TEST_USER_PHONE_NUMBER, response.getPhoneNumber());
        Assertions.assertEquals(TestDataFactory.TEST_USER_PROFILE_IMAGE_URL, response.getProfileImageUrl());
        Assertions.assertEquals("2024-01-01T12:00:00Z", response.getCreatedAt().toString());
        Assertions.assertEquals("2024-01-02T12:00:00Z", response.getUpdatedAt().toString());
    }

}
