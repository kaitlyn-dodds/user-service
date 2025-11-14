package kdodds.userservice.models.responses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kdodds.userservice.dto.responses.UserAddressesResponseDto;
import kdodds.userservice.utils.TestDataFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserAddressesResponseDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test serialization of UserAddressesResponse.
     */
    @Test
    public void testSerialization() throws JsonProcessingException {
        UserAddressesResponseDto response = TestDataFactory.createTestUserAddressesDto();

        String expectedJson = "{"
            + "\"links\":[],"
            + "\"user_id\":\"" + TestDataFactory.TEST_USER_ID + "\","
            + "\"addresses\":["
            + "{"
            + "\"links\":[],"
            + "\"address_id\":\"" + response.getAddresses().getFirst().getAddressId() + "\","
            + "\"user_id\":\"" + TestDataFactory.TEST_USER_ID + "\","
            + "\"address_type\":\"" + TestDataFactory.TEST_USER_ADDRESS_TYPE + "\","
            + "\"address_line_1\":\"" + TestDataFactory.TEST_USER_ADDRESS_LINE_1 + "\","
            + "\"city\":\"" + TestDataFactory.TEST_USER_CITY + "\","
            + "\"state\":\"" + TestDataFactory.TEST_USER_STATE + "\","
            + "\"zip_code\":\"" + TestDataFactory.TEST_USER_ZIP_CODE + "\","
            + "\"country\":\"" + TestDataFactory.TEST_USER_COUNTRY + "\","
            + "\"created_at\":\"" + response.getAddresses().getFirst().getCreatedAt().toString() + "\","
            + "\"updated_at\":\"" + response.getAddresses().getFirst().getUpdatedAt().toString() + "\""
            + "}]"
            + "}";

        Assertions.assertEquals(expectedJson, objectMapper.writeValueAsString(response));
    }

    /**
     * Test deserialization of UserAddressesResponse.
     */
    @Test
    public void testDeserialization() throws JsonProcessingException {
        UserAddressesResponseDto response = TestDataFactory.createTestUserAddressesDto();


        String jsonString = "{"
            + "\"user_id\":\"" + TestDataFactory.TEST_USER_ID + "\","
            + "\"addresses\":["
            + "{"
            + "\"address_id\":\"" + response.getAddresses().getFirst().getAddressId() + "\","
            + "\"user_id\":\"" + TestDataFactory.TEST_USER_ID + "\","
            + "\"address_type\":\"" + TestDataFactory.TEST_USER_ADDRESS_TYPE + "\","
            + "\"address_line_1\":\"" + TestDataFactory.TEST_USER_ADDRESS_LINE_1 + "\","
            + "\"city\":\"" + TestDataFactory.TEST_USER_CITY + "\","
            + "\"state\":\"" + TestDataFactory.TEST_USER_STATE + "\","
            + "\"zip_code\":\"" + TestDataFactory.TEST_USER_ZIP_CODE + "\","
            + "\"country\":\"" + TestDataFactory.TEST_USER_COUNTRY + "\","
            + "\"created_at\":\"" + response.getAddresses().getFirst().getCreatedAt().toString() + "\","
            + "\"updated_at\":\"" + response.getAddresses().getFirst().getUpdatedAt().toString() + "\""
            + "}]"
            + "}";

        UserAddressesResponseDto result = objectMapper.readValue(jsonString, UserAddressesResponseDto.class);

        Assertions.assertEquals(TestDataFactory.TEST_USER_ID, response.getUserId());
        Assertions.assertEquals(1, response.getAddresses().size());
        Assertions.assertEquals(result.getAddresses().getFirst(), response.getAddresses().getFirst());
    }

}
