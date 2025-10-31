package kdodds.userservice.assemblers;

import kdodds.userservice.controllers.v1.UserAddressController;
import kdodds.userservice.controllers.v1.UserController;
import kdodds.userservice.controllers.v1.UserProfileController;
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
public class UserModelAssembler
    implements RepresentationModelAssembler<UserResponseDto, EntityModel<UserResponseDto>> {

    /**
     * Converts a UserResponseDto to an EntityModel with HATEOAS links.
     *
     * @param userDto The UserResponseDto to convert.
     * @return EntityModel<UserResponseDto> with HATEOAS links.
     */
    @Override
    public EntityModel<UserResponseDto> toModel(UserResponseDto userDto) {
        // add links to top level dto
        assembleUserLinks(userDto);

        // add links for addresses on userDto
        if (userDto.getAddresses() != null) {
            userDto.getAddresses().forEach(UserModelAssembler::assembleUserAddressLinks);
        }

        return EntityModel.of(userDto);
    }

    private static void assembleUserLinks(UserResponseDto userDto) {
        try {
            userDto.add(linkTo(methodOn(UserController.class) // self
                .getUserByUserId(userDto.getUserId())).withSelfRel());
            userDto.add(linkTo(methodOn(UserProfileController.class) // profile
                .getUserProfileByUserId(userDto.getUserId())).withRel("profile"));
            userDto.add(linkTo(methodOn(UserAddressController.class) // addresses
                .getUserAddressesByUserId(userDto.getUserId())).withRel("addresses"));
            userDto.add(linkTo(methodOn(UserController.class)
                .getAllUsersPaginated(0, 0)).withRel("collection"));
        } catch (Exception e) {
            log.error("Error creating links for UserResponseDto: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void assembleUserAddressLinks(UserAddressResponseDto userAddressDto) {
        // add self, user links
        try {
            userAddressDto.add(linkTo(methodOn(UserAddressController.class) // self)
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
