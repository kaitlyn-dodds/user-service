package kdodds.userservice.controllers.v1;

import kdodds.userservice.assemblers.UserProfileModelAssembler;
import kdodds.userservice.dto.responses.UserProfileResponseDto;
import kdodds.userservice.exceptions.models.exceptions.InvalidUserIdException;
import kdodds.userservice.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users/{userId}/profile")
@AllArgsConstructor
public class UserProfileController {

    private UserService userService;

    private UserProfileModelAssembler userProfileModelAssembler;

    /**
     * Get user profile for a given user id.
     *
     * @param userId Unique user id of the user.
     * @return UserProfile object.
     */
    @GetMapping("")
    public ResponseEntity<EntityModel<UserProfileResponseDto>> getUserProfileByUserId(@PathVariable String userId)
        throws Exception {
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
        }

        UserProfileResponseDto userProfileResponseDto = userService.getUserProfileDtoByUserId(userId);

        return new ResponseEntity<>(
            userProfileModelAssembler.toModel(userProfileResponseDto),
            HttpStatus.OK
        );
    }

}
