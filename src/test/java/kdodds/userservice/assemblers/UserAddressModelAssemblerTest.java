package kdodds.userservice.assemblers;

import kdodds.userservice.dto.responses.UserAddressResponseDto;
import kdodds.userservice.utils.TestDataFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.EntityModel;

@SpringBootTest
public class UserAddressModelAssemblerTest {

    private UserAddressModelAssembler userAddressModelAssembler;

    /**
     * Setup for each test.
     */
    @BeforeEach
    public void setup() {
        userAddressModelAssembler = new UserAddressModelAssembler();
    }

    /**
     * Test that the userAddressModelAssembler toModel method returns a UserAddressResponseDto wrapped in an
     * EntityModel. Also verify that all expected links are present.
     */
    @Test
    public void testToModel() {
        UserAddressResponseDto dto = TestDataFactory.createTestUserAddressDto(TestDataFactory.TEST_USER_ID);

        EntityModel<UserAddressResponseDto> model = userAddressModelAssembler.toModel(dto);

        // verify that the model contains the expected dto
        UserAddressResponseDto modelDto = model.getContent();
        Assertions.assertNotNull(modelDto);

        // verify that the expected links are present (self, user, profile, addresses)
        Assertions.assertNotNull(model.getLinks());
        Assertions.assertTrue(modelDto.getLinks().hasSize(4));
        modelDto.getLink("self").orElseThrow(() -> new AssertionError("Missing self link"));
        modelDto.getLink("user").orElseThrow(() -> new AssertionError("Missing user link"));
        modelDto.getLink("profile").orElseThrow(() -> new AssertionError("Missing profile link"));
        modelDto.getLink("addresses").orElseThrow(() -> new AssertionError("Missing addresses link"));
    }

}
