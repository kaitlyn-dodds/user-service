package kdodds.userservice.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import kdodds.userservice.entities.UserAddress;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Jacksonized
@Data
@Builder
public class UserAddressResponseDto extends RepresentationModel<UserAddressResponseDto> {

    @JsonProperty("address_id")
    private String addressId;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("address_type")
    private String addressType; // billing, shipping, home, etc.

    @JsonProperty("address_line_1")
    private String addressLine1;

    @JsonProperty("address_line_2")
    private String addressLine2;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("zip_code")
    private String zipCode;

    @JsonProperty("country")
    private String country;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("updated_at")
    private Instant updatedAt;

    /**
     * Helper method to convert an entity to a DTO.
     *
     * @param userAddress The entity to convert.
     * @return UserAddressResponseDto
     */
    public static UserAddressResponseDto fromEntity(UserAddress userAddress) {
        return UserAddressResponseDto.builder()
            .addressId(userAddress.getId().toString())
            .userId(userAddress.getUser().getId().toString())
            .addressType(userAddress.getAddressType())
            .addressLine1(userAddress.getAddressLine1())
            .addressLine2(userAddress.getAddressLine2())
            .city(userAddress.getCity())
            .state(userAddress.getState())
            .zipCode(userAddress.getZipCode())
            .country(userAddress.getCountry())
            .createdAt(userAddress.getCreatedAt())
            .updatedAt(userAddress.getUpdatedAt())
            .build();
    }

}
