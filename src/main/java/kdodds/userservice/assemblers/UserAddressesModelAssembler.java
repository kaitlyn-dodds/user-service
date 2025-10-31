package kdodds.userservice.assemblers;

import kdodds.userservice.controllers.v1.UserAddressController;
import kdodds.userservice.controllers.v1.UserController;
import kdodds.userservice.dto.responses.UserAddressResponseDto;
import kdodds.userservice.dto.responses.UserAddressesResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@Slf4j
public class UserAddressesModelAssembler
    implements RepresentationModelAssembler<UserAddressesResponseDto, EntityModel<UserAddressesResponseDto>> {

    /**
     * Converts a UserAddressesResponseDto to an EntityModel.
     *
     * @param userAddressesDto The UserAddressesResponseDto to convert.
     * @return EntityModel<UserAddressesResponseDto>.
     */
    @Override
    public EntityModel<UserAddressesResponseDto> toModel(UserAddressesResponseDto userAddressesDto) {
        // add links to top level dto
        try {
            userAddressesDto.add(linkTo(methodOn(UserAddressController.class) // self)
                .getUserAddressesByUserId(userAddressesDto.getUserId())).withSelfRel());
            userAddressesDto.add(linkTo(methodOn(UserController.class) // user
                .getUserByUserId(userAddressesDto.getUserId())).withRel("user"));
        } catch (Exception ex) {
            log.error("Error creating links for UserAddressesResponseDto: {}", ex.getMessage());
            throw new RuntimeException(ex);
        }

        // add links for each address in the list
        if (userAddressesDto.getAddresses() != null && !userAddressesDto.getAddresses().isEmpty()) {
            userAddressesDto.getAddresses().forEach(UserAddressesModelAssembler::assembleLinks);
        }

        return EntityModel.of(userAddressesDto);
    }

    private static void assembleLinks(UserAddressResponseDto userAddressDto) {
        // add self and parent user links
        try {
            userAddressDto.add(linkTo(methodOn(UserAddressController.class) // self)
                .getUserAddressById(
                    userAddressDto.getUserId(),
                    userAddressDto.getAddressId()
                )).withSelfRel());
            userAddressDto.add(linkTo(methodOn(UserController.class) // user
                .getUserByUserId(userAddressDto.getUserId())).withRel("user"));
        } catch (Exception ex) {
            log.error("Error creating links for UserAddressResponseDto: {}", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

}
