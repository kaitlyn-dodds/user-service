package kdodds.userservice.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import kdodds.userservice.entities.UserProfile;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
@Builder
@Jacksonized
public class UserProfileResponseDto extends RepresentationModel<UserProfileResponseDto> {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("updated_at")
    private Instant updatedAt;

    /**
     * Helper method to convert an entity to a DTO.
     *
     * @param entity The entity to convert.
     * @return UserProfileResponseDto
     */
    public static UserProfileResponseDto fromEntity(UserProfile entity) {
        if (entity.getUserId() == null) {
            // TODO: throw an exception
            return null;
        }

        return UserProfileResponseDto.builder()
            .userId(entity.getUserId().toString())
            .firstName(entity.getFirstName())
            .lastName(entity.getLastName())
            .phoneNumber(entity.getPhoneNumber())
            .profileImageUrl(entity.getProfileImageUrl())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

}
