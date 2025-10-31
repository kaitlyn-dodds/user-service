package kdodds.userservice.assemblers;

import kdodds.userservice.dto.responses.UserAddressesResponseDto;
import kdodds.userservice.utils.TestDataFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.EntityModel;

@SpringBootTest
public class UserAddressesModelAssemblerTest {

    private UserAddressesModelAssembler userAddressesModelAssembler;

    /**
     * Setup for each test.
     */
    @BeforeEach
    public void setup() {
        userAddressesModelAssembler = new UserAddressesModelAssembler();
    }

    /**
     * Test that the userAddressesModelAssembler toModel method returns a UserAddressesResponseDto wrapped in an
     * EntityModel. Also verify that all expected links are present.
     */
    @Test
    public void testToModel() {
        UserAddressesResponseDto dto = TestDataFactory.createTestUserAddressesDto();

        EntityModel<UserAddressesResponseDto> model = userAddressesModelAssembler.toModel(dto);

        // verify that the model contains the expected dto
        UserAddressesResponseDto modelDto = model.getContent();
        Assertions.assertNotNull(modelDto);

        // verify that the expected links are present (self, user)
        Assertions.assertNotNull(model.getLinks());
        Assertions.assertTrue(modelDto.getLinks().hasSize(2));
        modelDto.getLink("self").orElseThrow(() -> new AssertionError("Missing self link"));
        modelDto.getLink("user").orElseThrow(() -> new AssertionError("Missing user link"));

        // verify that each address has a self, user
        modelDto.getAddresses().forEach(addressDto -> {
            Assertions.assertNotNull(addressDto.getLinks());
            Assertions.assertTrue(addressDto.getLinks().hasSize(2));
            addressDto.getLink("self").orElseThrow(() -> new AssertionError("Missing self link"));
            addressDto.getLink("user").orElseThrow(() -> new AssertionError("Missing user link"));
        });
    }

}
