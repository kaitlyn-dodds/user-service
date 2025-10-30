package kdodds.userservice.assemblers;

import kdodds.userservice.controllers.v1.UserAddressController;
import kdodds.userservice.controllers.v1.UserController;
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
        try {
            userDto.add(linkTo(methodOn(UserController.class) // self
                .getUserByUserId(userDto.getUserId())).withSelfRel());
            userDto.add(linkTo(methodOn(UserController.class) // profile
                .getUserProfileByUserId(userDto.getUserId())).withRel("profile"));
            userDto.add(linkTo(methodOn(UserAddressController.class) // addresses
                .getUserAddressesByUserId(userDto.getUserId())).withRel("addresses"));
        } catch (Exception e) {
            log.error("Error creating links for UserResponseDto: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        // add links for addresses
        if (userDto.getAddresses() != null) {
            userDto.getAddresses().forEach(addressDto -> {
                try {
                    addressDto.add(linkTo(methodOn(UserAddressController.class)
                        .getUserAddressById(
                            userDto.getUserId(),
                            addressDto.getAddressId()
                        )).withSelfRel());
                } catch (Exception e) {
                    log.error("Error creating link for UserAddressResponseDto: {}", e.getMessage());
                    throw new RuntimeException(e);
                }
            });
        }

        return EntityModel.of(userDto);
    }

}
