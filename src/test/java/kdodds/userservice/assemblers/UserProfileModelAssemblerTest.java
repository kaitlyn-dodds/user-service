package kdodds.userservice.assemblers;

import kdodds.userservice.dto.responses.UserProfileResponseDto;
import kdodds.userservice.utils.TestDataFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.EntityModel;

@SpringBootTest
public class UserProfileModelAssemblerTest {

    private UserProfileModelAssembler userProfileModelAssembler;

    /**
     * Setup for each test.
     */
    @BeforeEach
    public void setup() {
        userProfileModelAssembler = new UserProfileModelAssembler();
    }

    /**
     * Test that the userProfileModelAssembler toModel method returns a UserProfileResponseDto wrapped in an
     * EntityModel. Also verify that all expected links are present.
     */
    @Test
    public void testToModel() {
        UserProfileResponseDto dto = TestDataFactory.createTestUserProfileDto(TestDataFactory.TEST_USER_ID);

        EntityModel<UserProfileResponseDto> model = userProfileModelAssembler.toModel(dto);

        // verify that the model contains the expected dto
        UserProfileResponseDto modelDto = model.getContent();
        Assertions.assertNotNull(modelDto);

        // verify that links are present (self, addresses, user)
        Assertions.assertNotNull(model.getLinks());
        Assertions.assertTrue(modelDto.getLinks().hasSize(3));
        modelDto.getLink("self").orElseThrow(() -> new AssertionError("Missing self link"));
        modelDto.getLink("addresses").orElseThrow(() -> new AssertionError("Missing addresses link"));
        modelDto.getLink("user").orElseThrow(() -> new AssertionError("Missing user link"));
    }

}
