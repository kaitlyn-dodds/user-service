package kdodds.userservice.controllers.v1;

import kdodds.userservice.assemblers.UserAddressModelAssembler;
import kdodds.userservice.assemblers.UserAddressesModelAssembler;
import kdodds.userservice.dto.responses.UserAddressResponseDto;
import kdodds.userservice.dto.responses.UserAddressesResponseDto;
import kdodds.userservice.exceptions.models.exceptions.InvalidRequestDataException;
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
@RequestMapping("/v1/users/{userId}/addresses")
@AllArgsConstructor
public class UserAddressController {

    private UserService userService;

    private UserAddressModelAssembler userAddressModelAssembler;

    private UserAddressesModelAssembler userAddressesModelAssembler;

    /**
     * Get user addresses for a given user id.
     *
     * @param userId Unique user id of the user.
     * @return UserAddressesResponse object.
     */
    @GetMapping("")
    public ResponseEntity<EntityModel<UserAddressesResponseDto>> getUserAddressesByUserId(@PathVariable String userId)
        throws Exception {
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
        }

        UserAddressesResponseDto addressesResponseDto = userService.getUserAddressesDtoByUserId(userId);

        return new ResponseEntity<>(
            userAddressesModelAssembler.toModel(addressesResponseDto),
            HttpStatus.OK
        );
    }

    /**
     * Get a user address by address id.
     *
     * @param userId The user id to use for the test data.
     * @param addressId The address id to use for the test data.
     * @return UserAddressResponseDto
     */
    @GetMapping("/{addressId}")
    public ResponseEntity<EntityModel<UserAddressResponseDto>> getUserAddressById(
        @PathVariable String userId,
        @PathVariable String addressId
    ) throws Exception {
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
        }

        if (addressId == null || addressId.isEmpty()) {
            throw new InvalidRequestDataException("Invalid null or empty address id");
        }

        UserAddressResponseDto response = userService.getUserAddressDtoById(userId, addressId);

        return new ResponseEntity<>(
            userAddressModelAssembler.toModel(response),
            HttpStatus.OK
        );
    }


}
