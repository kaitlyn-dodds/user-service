package kdodds.userservice.assemblers;

import kdodds.userservice.controllers.v1.UserController;
import kdodds.userservice.dto.responses.UserAddressResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@Slf4j
public class UserAddressModelAssembler
    implements RepresentationModelAssembler<UserAddressResponseDto, EntityModel<UserAddressResponseDto>> {

    /**
     * Converts a UserAddressResponseDto to an EntityModel with HATEOAS links.
     *
     * @param userAddressDto The UserAddressResponseDto to convert.
     * @return EntityModel<UserAddressResponseDto> with HATEOAS links.
     */
    @Override
    public EntityModel<UserAddressResponseDto> toModel(UserAddressResponseDto userAddressDto) {
        // add links
        try {
            userAddressDto.add(linkTo(methodOn(UserController.class) // self)
                .getUserAddressById(
                    userAddressDto.getUserId(),
                    userAddressDto.getAddressId()
                )).withSelfRel());
            userAddressDto.add(linkTo(methodOn(UserController.class) // user
                .getUserByUserId(userAddressDto.getUserId())).withRel("user"));
            userAddressDto.add(linkTo(methodOn(UserController.class) // profile
                .getUserProfileByUserId(userAddressDto.getUserId())).withRel("profile"));
            userAddressDto.add(linkTo(methodOn(UserController.class) // addresses
                .getUserAddressesByUserId(userAddressDto.getUserId())).withRel("addresses"));
        } catch (Exception e) {
            log.error("Error creating links for UserAddressResponseDto: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        return EntityModel.of(userAddressDto);
    }

}
