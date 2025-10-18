package kdodds.userservice.models.responses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kdodds.userservice.models.UserAddress;
import kdodds.userservice.utils.TestDataFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class UserAddressesResponseTest {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test serialization of UserAddressesResponse.
     */
    @Test
    public void testSerialization() throws JsonProcessingException {
        List<UserAddress> addresses = List.of(
            TestDataFactory.createTestUserAddress(TestDataFactory.TEST_USER_ID)
        );

        UserAddressesResponse response = UserAddressesResponse.builder()
            .userId(TestDataFactory.TEST_USER_ID)
            .addresses(addresses)
            .build();

        String expectedJson = "{"
            + "\"user_id\":\"" + TestDataFactory.TEST_USER_ID + "\","
            + "\"addresses\":["
            + "{"
            + "\"id\":\"" + addresses.getFirst().getId() + "\","
            + "\"user_id\":\"" + TestDataFactory.TEST_USER_ID + "\","
            + "\"address_type\":\"" + TestDataFactory.TEST_USER_ADDRESS_TYPE + "\","
            + "\"address_line1\":\"" + TestDataFactory.TEST_USER_ADDRESS_LINE_1 + "\","
            + "\"city\":\"" + TestDataFactory.TEST_USER_CITY + "\","
            + "\"state\":\"" + TestDataFactory.TEST_USER_STATE + "\","
            + "\"zip_code\":\"" + TestDataFactory.TEST_USER_ZIP_CODE + "\","
            + "\"country\":\"" + TestDataFactory.TEST_USER_COUNTRY + "\","
            + "\"created_at\":\"" + addresses.getFirst().getCreatedAt().toString() + "\","
            + "\"updated_at\":\"" + addresses.getFirst().getUpdatedAt().toString() + "\""
            + "}]"
            + "}";

        Assertions.assertEquals(expectedJson, objectMapper.writeValueAsString(response));
    }

    /**
     * Test deserialization of UserAddressesResponse.
     */
    @Test
    public void testDeserialization() throws JsonProcessingException {
        List<UserAddress> addresses = List.of(
            TestDataFactory.createTestUserAddress(TestDataFactory.TEST_USER_ID)
        );

        String jsonString = "{"
            + "\"user_id\":\"" + TestDataFactory.TEST_USER_ID + "\","
            + "\"addresses\":["
            + "{"
            + "\"id\":\"" + addresses.getFirst().getId() + "\","
            + "\"user_id\":\"" + TestDataFactory.TEST_USER_ID + "\","
            + "\"address_type\":\"" + TestDataFactory.TEST_USER_ADDRESS_TYPE + "\","
            + "\"address_line1\":\"" + TestDataFactory.TEST_USER_ADDRESS_LINE_1 + "\","
            + "\"city\":\"" + TestDataFactory.TEST_USER_CITY + "\","
            + "\"state\":\"" + TestDataFactory.TEST_USER_STATE + "\","
            + "\"zip_code\":\"" + TestDataFactory.TEST_USER_ZIP_CODE + "\","
            + "\"country\":\"" + TestDataFactory.TEST_USER_COUNTRY + "\","
            + "\"created_at\":\"" + addresses.getFirst().getCreatedAt().toString() + "\","
            + "\"updated_at\":\"" + addresses.getFirst().getUpdatedAt().toString() + "\""
            + "}]"
            + "}";

        UserAddressesResponse response = objectMapper.readValue(jsonString, UserAddressesResponse.class);

        Assertions.assertEquals(TestDataFactory.TEST_USER_ID, response.getUserId());
        Assertions.assertEquals(1, response.getAddresses().size());
        Assertions.assertEquals(addresses.getFirst(), response.getAddresses().getFirst());
    }

}
