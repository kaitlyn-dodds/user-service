package kdodds.userservice.assemblers;

import kdodds.userservice.controllers.v1.UserAddressController;
import kdodds.userservice.controllers.v1.UserController;
import kdodds.userservice.controllers.v1.UserProfileController;
import kdodds.userservice.dto.responses.PagedUsersResponseDto;
import kdodds.userservice.dto.responses.UserAddressResponseDto;
import kdodds.userservice.dto.responses.UserResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@Slf4j
public class PagedUsersModelAssembler
    implements RepresentationModelAssembler<PagedUsersResponseDto, EntityModel<PagedUsersResponseDto>> {

    /**
     * Converts a PagedUsersResponseDto to an EntityMode with HATEOAS links.
     *
     * @param pagedUsersDto The PagedUsersResponseDto to convert.
     * @return EntityModel<PagedUsersResponseDto> with HATEOAS links.
     */
    @Override
    public EntityModel<PagedUsersResponseDto> toModel(PagedUsersResponseDto pagedUsersDto) {
        // add links for pagination (self, next, prev, first, last)
        buildPaginationLinks(pagedUsersDto);

        // add links for each user in the page
        for (UserResponseDto userDto : pagedUsersDto.getUsers()) {
            // add links to the user dto
            assembleUserLinks(userDto);

            // add links for addresses
            if (userDto.getAddresses() != null) {
                userDto.getAddresses().forEach(PagedUsersModelAssembler::assembleUserAddressLinks);
            }
        }

        return EntityModel.of(pagedUsersDto);
    }

    private static void buildPaginationLinks(PagedUsersResponseDto pagedUsersDto) {
        try {
            // self
            pagedUsersDto.add(linkTo(methodOn(UserController.class)
                .getAllUsersPaginated(
                    pagedUsersDto.getPage().getPage(),
                    pagedUsersDto.getPage().getSize(),
                    null,
                    null,
                    null,
                    null,
                    null
                )).withSelfRel());

            // next (not on last page)
            if (pagedUsersDto.getPage().getPage() < pagedUsersDto.getPage().getTotalPages() - 1) {
                pagedUsersDto.add(linkTo(methodOn(UserController.class)
                    .getAllUsersPaginated(
                        pagedUsersDto.getPage().getPage() + 1,
                        pagedUsersDto.getPage().getSize(),
                        null,
                        null,
                        null,
                        null,
                        null
                    )).withRel("next"));
            }

            // prev (not on first page)
            if (pagedUsersDto.getPage().getPage() > 0) {
                pagedUsersDto.add(linkTo(methodOn(UserController.class)
                    .getAllUsersPaginated(
                        pagedUsersDto.getPage().getPage() - 1,
                        pagedUsersDto.getPage().getSize(),
                        null,
                        null,
                        null,
                        null,
                        null
                    )).withRel("prev"));
            }

            // first (not on first page)
            if (pagedUsersDto.getPage().getPage() > 0) {
                pagedUsersDto.add(linkTo(methodOn(UserController.class)
                    .getAllUsersPaginated(
                        0,
                        pagedUsersDto.getPage().getSize(),
                        null,
                        null,
                        null,
                        null,
                        null
                    )).withRel("first"));
            }

            // last (more than one page, also not on last page)
            if (pagedUsersDto.getPage().getTotalPages() > 0
                && pagedUsersDto.getPage().getPage() < pagedUsersDto.getPage().getTotalPages() - 1) {
                pagedUsersDto.add(linkTo(methodOn(UserController.class)
                    .getAllUsersPaginated(
                        pagedUsersDto.getPage().getTotalPages() - 1,
                        pagedUsersDto.getPage().getSize(),
                        null,
                        null,
                        null,
                        null,
                        null
                    )).withRel("last"));
            }
        } catch (Exception e) {
            log.error("Error creating pagination links for PagedUsersResponseDto: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void assembleUserLinks(UserResponseDto userDto) {
        // add self, profile, addresses
        try {
            userDto.add(linkTo(methodOn(UserController.class) // self
                .getUserByUserId(userDto.getUserId())).withSelfRel());
            userDto.add(linkTo(methodOn(UserProfileController.class) // profile
                .getUserProfileByUserId(userDto.getUserId())).withRel("profile"));
            userDto.add(linkTo(methodOn(UserAddressController.class) // addresses
                .getUserAddressesByUserId(userDto.getUserId())).withRel("addresses"));
        } catch (Exception e) {
            log.error("Error creating links for UserResponseDto: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void assembleUserAddressLinks(UserAddressResponseDto userAddressDto) {
        // add self, user
        try {
            userAddressDto.add(linkTo(methodOn(UserAddressController.class) // self
                .getUserAddressById(
                    userAddressDto.getUserId(),
                    userAddressDto.getAddressId()
                )).withSelfRel());
            userAddressDto.add(linkTo(methodOn(UserController.class) // user
                .getUserByUserId(userAddressDto.getUserId())).withRel("user"));
        } catch (Exception e) {
            log.error("Error creating links for UserAddressResponseDto: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
