package kdodds.userservice.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import kdodds.userservice.entities.User;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Jacksonized
@Data
@Builder
@Slf4j
public class UserResponseDto extends RepresentationModel<UserResponseDto> {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    @JsonProperty("addresses")
    private List<UserAddressResponseDto> addresses;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("updated_at")
    private Instant updatedAt;

    /**
     * Helper method to convert an entity to a DTO.
     *
     * @param user The entity to convert.
     * @return UserResponseDto
     */
    public static UserResponseDto fromEntity(User user) {
        if (user.getId() == null) {
            // TODO: throw an exception
            return null;
        }

        UserResponseDto response = UserResponseDto.builder()
            .userId(user.getId().toString())
            .username(user.getUsername())
            .email(user.getEmail())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();

        // convert addresses
        List<UserAddressResponseDto> addresses = new ArrayList<>();
        log.debug("User has {} addresses in database", user.getAddresses().size());
        if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
            addresses = user.getAddresses()
                .stream()
                .map(UserAddressResponseDto::fromEntity)
                .toList();
        }
        response.setAddresses(addresses);

        // set profile data if applicable
        if (user.getUserProfile() != null) {
            response.setFirstName(user.getUserProfile().getFirstName());
            response.setLastName(user.getUserProfile().getLastName());
            response.setPhoneNumber(user.getUserProfile().getPhoneNumber());
            response.setProfileImageUrl(user.getUserProfile().getProfileImageUrl());
        }

        return response;
    }

}
