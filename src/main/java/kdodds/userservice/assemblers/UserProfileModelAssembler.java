package kdodds.userservice.assemblers;

import kdodds.userservice.controllers.v1.UserAddressController;
import kdodds.userservice.controllers.v1.UserController;
import kdodds.userservice.dto.responses.UserProfileResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@Slf4j
public class UserProfileModelAssembler implements
    RepresentationModelAssembler<UserProfileResponseDto, EntityModel<UserProfileResponseDto>> {

    /**
     * Converts a UserProfileResponseDto to an EntityModel with HATEOAS links.
     *
     * @param userProfileDto The UserProfileResponseDto to convert.
     * @return EntityModel<UserProfileResponseDto> with HATEOAS links.
     */
    @Override
    public EntityModel<UserProfileResponseDto> toModel(UserProfileResponseDto userProfileDto) {

        try {
            userProfileDto.add(linkTo(methodOn(UserController.class) // self
                .getUserProfileByUserId(userProfileDto.getUserId())).withSelfRel());
            userProfileDto.add(linkTo(methodOn(UserController.class) // user
                .getUserByUserId(userProfileDto.getUserId())).withRel("user"));
            userProfileDto.add(linkTo(methodOn(UserAddressController.class) // addresses
                .getUserAddressesByUserId(userProfileDto.getUserId())).withRel("addresses"));
        } catch (Exception e) {
            log.error("Error creating links for UserProfileResponseDto: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        return EntityModel.of(userProfileDto);
    }

}
