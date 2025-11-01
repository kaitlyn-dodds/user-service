package kdodds.userservice.assemblers;

import kdodds.userservice.dto.responses.PagedUsersResponseDto;
import kdodds.userservice.utils.TestDataFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.EntityModel;

import java.util.List;

@SpringBootTest
public class PagedUsersModelAssemblerTest {

    private PagedUsersModelAssembler pagedUsersModelAssembler;

    /**
     * Setup for each test.
     */
    @BeforeEach
    public void setup() {
        pagedUsersModelAssembler = new PagedUsersModelAssembler();
    }

    /**
     * Test the PagedUsersModelAssembler toModel method returns a valid PagedUsersResponseDto wrapped in an EntityModel.
     * Also verify that all expected links are present.
     */
    @Test
    public void testToModel() {
        PagedUsersResponseDto response = TestDataFactory.createTestPagedUsersResponseDto(
            List.of(TestDataFactory.createTestUserResponseDto()),
            TestDataFactory.createTestPageDto(0, 2, 5, 10)
        );

        EntityModel<PagedUsersResponseDto> entityModel = pagedUsersModelAssembler.toModel(response);

        // verify that the model contains the expected dto
        PagedUsersResponseDto modelDto = entityModel.getContent();
        Assertions.assertNotNull(modelDto);

        // assert page
        Assertions.assertEquals(response.getPage().getPage(), modelDto.getPage().getPage());
        Assertions.assertEquals(response.getPage().getSize(), modelDto.getPage().getSize());
        Assertions.assertEquals(response.getPage().getTotalElements(), modelDto.getPage().getTotalElements());
        Assertions.assertEquals(response.getPage().getTotalPages(), modelDto.getPage().getTotalPages());

        // assert users
        Assertions.assertEquals(response.getUsers().size(), modelDto.getUsers().size());
        Assertions.assertEquals(
            response.getUsers().getFirst().getUserId(),
            modelDto.getUsers().getFirst().getUserId()
        );
        Assertions.assertEquals(
            response.getUsers().getFirst().getUsername(),
            modelDto.getUsers().getFirst().getUsername()
        );

        // verify links are present on top level dto (expect self, next, last)
        Assertions.assertNotNull(entityModel.getLinks());
        Assertions.assertTrue(modelDto.getLinks().hasSize(3));
        modelDto.getLink("self").orElseThrow(() -> new AssertionError("Missing self link"));
        modelDto.getLink("next").orElseThrow(() -> new AssertionError("Missing next link"));
        modelDto.getLink("last").orElseThrow(() -> new AssertionError("Missing last link"));

        // verify links are present on each user dto
        modelDto.getUsers().forEach(userDto -> {
            Assertions.assertNotNull(userDto.getLinks());
            Assertions.assertTrue(userDto.getLinks().hasSize(3));
            userDto.getLink("self").orElseThrow(() -> new AssertionError("Missing self link"));
            userDto.getLink("addresses").orElseThrow(() -> new AssertionError("Missing addresses link"));
            userDto.getLink("profile").orElseThrow(() -> new AssertionError("Missing profile link"));

            // verify that links are present on each address dto
            userDto.getAddresses().forEach(addressDto -> {
                Assertions.assertNotNull(addressDto.getLinks());
                Assertions.assertTrue(addressDto.getLinks().hasSize(2));
                addressDto.getLink("self").orElseThrow(() -> new AssertionError("Missing self link"));
                addressDto.getLink("user").orElseThrow(() -> new AssertionError("Missing user link"));
            });
        });

    }

    /**
     * Test that only self link is present when on the first and only page.
     */
    @Test
    public void testToModel_OnlyPage() {
        PagedUsersResponseDto response = TestDataFactory.createTestPagedUsersResponseDto(
            List.of(TestDataFactory.createTestUserResponseDto()),
            TestDataFactory.createTestPageDto(0, 1, 1, 1)
        );

        EntityModel<PagedUsersResponseDto> entityModel = pagedUsersModelAssembler.toModel(response);

        // verify that the model contains the expected dto
        PagedUsersResponseDto modelDto = entityModel.getContent();
        Assertions.assertNotNull(modelDto);

        // assert page links are present on top level dto (expect self)
        Assertions.assertNotNull(entityModel.getLinks());
        Assertions.assertTrue(modelDto.getLinks().hasSize(1));
        modelDto.getLink("self").orElseThrow(() -> new AssertionError("Missing self link"));
    }

    /**
     * Test that the self, next, last links are present when on the first of many pages.
     */
    @Test
    public void testToModel_FirstPage() {
        PagedUsersResponseDto response = TestDataFactory.createTestPagedUsersResponseDto(
            List.of(TestDataFactory.createTestUserResponseDto()),
            TestDataFactory.createTestPageDto(0, 2, 5, 10)
        );

        EntityModel<PagedUsersResponseDto> entityModel = pagedUsersModelAssembler.toModel(response);

        // verify that the model contains the expected dto
        PagedUsersResponseDto modelDto = entityModel.getContent();
        Assertions.assertNotNull(modelDto);

        // verify page links are present on top level dto (expect self, next, last)
        Assertions.assertNotNull(entityModel.getLinks());
        Assertions.assertTrue(modelDto.getLinks().hasSize(3));
        modelDto.getLink("self").orElseThrow(() -> new AssertionError("Missing self link"));
        modelDto.getLink("next").orElseThrow(() -> new AssertionError("Missing next link"));
        modelDto.getLink("last").orElseThrow(() -> new AssertionError("Missing last link"));
    }

    /**
     * Test that the self, prev, next, first, last links are present when on a middle page.
     */
    @Test
    public void testToModel_MiddlePage() {
        PagedUsersResponseDto response = TestDataFactory.createTestPagedUsersResponseDto(
            List.of(TestDataFactory.createTestUserResponseDto()),
            TestDataFactory.createTestPageDto(2, 2, 5, 10)
        );

        EntityModel<PagedUsersResponseDto> entityModel = pagedUsersModelAssembler.toModel(response);

        // verify that the model contains the expected dto
        PagedUsersResponseDto modelDto = entityModel.getContent();
        Assertions.assertNotNull(modelDto);

        // verify page links are present on top level dto (expect self, prev, next, first, last)
        Assertions.assertNotNull(entityModel.getLinks());
        Assertions.assertTrue(modelDto.getLinks().hasSize(5));
        modelDto.getLink("self").orElseThrow(() -> new AssertionError("Missing self link"));
        modelDto.getLink("prev").orElseThrow(() -> new AssertionError("Missing prev link"));
        modelDto.getLink("next").orElseThrow(() -> new AssertionError("Missing next link"));
        modelDto.getLink("first").orElseThrow(() -> new AssertionError("Missing first link"));
        modelDto.getLink("last").orElseThrow(() -> new AssertionError("Missing last link"));
    }

    /**
     * Test that the self, prev, first links are present when on the last page.
     */
    @Test
    public void testToModel_LastPage() {
        PagedUsersResponseDto response = TestDataFactory.createTestPagedUsersResponseDto(
            List.of(TestDataFactory.createTestUserResponseDto()),
            TestDataFactory.createTestPageDto(4, 2, 5, 10)
        );

        EntityModel<PagedUsersResponseDto> entityModel = pagedUsersModelAssembler.toModel(response);

        // verify that the model contains the expected dto
        PagedUsersResponseDto modelDto = entityModel.getContent();
        Assertions.assertNotNull(modelDto);

        // verify page links are present on top level dto (expect self, prev, first)
        Assertions.assertNotNull(entityModel.getLinks());
        Assertions.assertTrue(modelDto.getLinks().hasSize(3));
        modelDto.getLink("self").orElseThrow(() -> new AssertionError("Missing self link"));
        modelDto.getLink("prev").orElseThrow(() -> new AssertionError("Missing prev link"));
        modelDto.getLink("first").orElseThrow(() -> new AssertionError("Missing first link"));
    }

}
