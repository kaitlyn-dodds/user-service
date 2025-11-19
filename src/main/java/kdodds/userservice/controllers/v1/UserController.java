package kdodds.userservice.controllers.v1;

import kdodds.userservice.assemblers.PagedUsersModelAssembler;
import kdodds.userservice.assemblers.UserModelAssembler;
import kdodds.userservice.dto.requests.CreateUserRequestDto;
import kdodds.userservice.dto.requests.PatchUserRequestDto;
import kdodds.userservice.dto.responses.PagedUsersResponseDto;
import kdodds.userservice.dto.responses.UserResponseDto;
import kdodds.userservice.entities.User;
import kdodds.userservice.exceptions.models.exceptions.InvalidRequestDataException;
import kdodds.userservice.exceptions.models.exceptions.InvalidUserIdException;
import kdodds.userservice.repositories.specifications.UserSpecification;
import kdodds.userservice.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles all user related endpoints.
 */

@RestController()
@RequestMapping("/v1/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    private UserModelAssembler userModelAssembler;

    private PagedUsersModelAssembler pagedUsersModelAssembler;

    /**
     * Gets all users, paginated according to page and size parameters.
     */
    @GetMapping()
    public ResponseEntity<EntityModel<PagedUsersResponseDto>> getAllUsersPaginated(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String username
    ) throws Exception {
        // build the UserSpecification
        Specification<User> spec = UserSpecification.build(username);

        PagedUsersResponseDto response = userService.getAllUsersPaginated(page, size, spec);

        return new ResponseEntity<>(
            pagedUsersModelAssembler.toModel(response),
            HttpStatus.OK
        );
    }

    /**
     * Gets all user data for a given user id.
     *
     * @param userId The unique user id of the user
     * @return UserResponse wrapped in a ResponseEntity
     */
    @GetMapping("/{userId}")
    public ResponseEntity<EntityModel<UserResponseDto>> getUserByUserId(@PathVariable String userId) throws Exception {
        // check for null or invalid user id
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
        }

        UserResponseDto response = userService.getUserResponseDto(userId);

        return new ResponseEntity<>(
            userModelAssembler.toModel(response),
            HttpStatus.OK
        );
    }

    /**
     * Creates a new user with a unique username and email.
     */
    @PostMapping()
    public ResponseEntity<EntityModel<UserResponseDto>> createUser(@RequestBody CreateUserRequestDto request)
        throws Exception {
        // request body must be included
        if (request == null) {
            throw new InvalidRequestDataException("Request body must be included");
        }

        // validate request body
        validateCreateUserRequest(request);

        // attempt to create complete user
        UserResponseDto user = userService.createUserAndProfileAndAddress(request);

        return new ResponseEntity<>(
            userModelAssembler.toModel(user),
            HttpStatus.CREATED
        );
    }

    /**
     * Deletes a user by user id.
     *
     * @param userId The user id of the user to delete.
     * @return ResponseEntity<Void>
     * @throws Exception Throws an exception if the user cannot be deleted.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) throws Exception {
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
        }

        // call service
        userService.deleteUserByUserId(userId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Updates a user's data.
     *
     * @param userId The user id of the user to update.
     * @param request The PatchUserRequestDto to use for the data.
     * @return UserResponseDto wrapped in a ResponseEntity.
     * @throws Exception Throws an exception if the request is invalid or the attempt to update the user fails.
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<EntityModel<UserResponseDto>> updateUser(
        @PathVariable String userId,
        @RequestBody PatchUserRequestDto request
    ) throws Exception {
        if (userId == null || userId.isEmpty()) {
            throw new InvalidUserIdException();
        }

        UserResponseDto updatedUser = userService.updateUser(userId, request);

        return new ResponseEntity<>(
            userModelAssembler.toModel(updatedUser),
            HttpStatus.OK
        );
    }

    private void validateCreateUserRequest(CreateUserRequestDto request) {
        // username must be included
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            throw new InvalidRequestDataException("Username must be included");
        }

        // email must be included
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new InvalidRequestDataException("Email must be included");
        }

        // first name must be included
        if (request.getFirstName() == null || request.getFirstName().isEmpty()) {
            throw new InvalidRequestDataException("First name must be included");
        }

        // last name must be included
        if (request.getLastName() == null || request.getLastName().isEmpty()) {
            throw new InvalidRequestDataException("Last name must be included");
        }

        // password must be included
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new InvalidRequestDataException("Password must be included");
        }

        // phone number must be included
        if (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty()) {
            throw new InvalidRequestDataException("Phone number must be included");
        }
    }

}
