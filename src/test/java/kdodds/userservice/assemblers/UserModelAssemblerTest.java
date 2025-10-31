package kdodds.userservice.assemblers;

import kdodds.userservice.dto.responses.UserResponseDto;
import kdodds.userservice.utils.TestDataFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.EntityModel;

@SpringBootTest
public class UserModelAssemblerTest {

    private UserModelAssembler userModelAssembler;

    /**
     * Setup for each test.
     */
    @BeforeEach
    public void setup() {
        userModelAssembler = new UserModelAssembler();
    }

    /**
     * Test that the userModelAssembler toModel method returns a UserResponseDto wrapped in an EntityModel. Also verify
     * that all expected links are present.
     */
    @Test
    public void testToModel() {
        UserResponseDto dto = TestDataFactory.createTestUserResponseDto();

        EntityModel<UserResponseDto> model = userModelAssembler.toModel(dto);

        // verify that the model contains the expected dto
        UserResponseDto modelDto = model.getContent();
        Assertions.assertNotNull(modelDto);

        // verify that links are present (self, profile, addresses)
        Assertions.assertNotNull(model.getLinks());
        Assertions.assertTrue(modelDto.getLinks().hasSize(4));
        modelDto.getLink("self").orElseThrow(() -> new AssertionError("Missing self link"));
        modelDto.getLink("profile").orElseThrow(() -> new AssertionError("Missing profile link"));
        modelDto.getLink("addresses").orElseThrow(
            () -> new AssertionError("Missing addresses link"));
        modelDto.getLink("collection").orElseThrow(
            () -> new AssertionError("Missing collection link"));

        // verify that each address has a self, user, addresses, profile link
        modelDto.getAddresses().forEach(addressDto -> {
            Assertions.assertNotNull(addressDto.getLinks());
            Assertions.assertTrue(addressDto.getLinks().hasSize(2));
            addressDto.getLink("self").orElseThrow(() -> new AssertionError("Missing self link"));
            addressDto.getLink("user").orElseThrow(() -> new AssertionError("Missing user link"));
        });
    }

}
