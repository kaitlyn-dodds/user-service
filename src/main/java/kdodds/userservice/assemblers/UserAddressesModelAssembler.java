package kdodds.userservice.assemblers;

import kdodds.userservice.controllers.v1.UserController;
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
            userAddressesDto.add(linkTo(methodOn(UserController.class) // self)
                .getUserAddressesByUserId(userAddressesDto.getUserId())).withSelfRel());
            userAddressesDto.add(linkTo(methodOn(UserController.class) // user
                .getUserByUserId(userAddressesDto.getUserId())).withRel("user"));
            userAddressesDto.add(linkTo(methodOn(UserController.class) // profile
                .getUserProfileByUserId(userAddressesDto.getUserId())).withRel("profile"));
        } catch (Exception ex) {
            log.error("Error creating links for UserAddressesResponseDto: {}", ex.getMessage());
            throw new RuntimeException(ex);
        }

        // add links for each address in the list
        if (userAddressesDto.getAddresses() != null && !userAddressesDto.getAddresses().isEmpty()) {
            userAddressesDto.getAddresses().forEach(address -> {
                try {
                    address.add(linkTo(methodOn(UserController.class) // self
                        .getUserAddressById(
                            userAddressesDto.getUserId(),
                            address.getAddressId()
                        )).withSelfRel());
                } catch (Exception e) {
                    log.error("Error creating links for UserAddressResponseDto: {}", e.getMessage());
                    throw new RuntimeException(e);
                }
            });
        }

        return EntityModel.of(userAddressesDto);
    }

}
