package kdodds.userservice.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import kdodds.userservice.entities.UserAddress;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.springframework.hateoas.RepresentationModel;

import java.util.ArrayList;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Jacksonized
@Data
@Builder
public class UserAddressesResponseDto extends RepresentationModel<UserAddressesResponseDto> {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("addresses")
    private List<UserAddressResponseDto> addresses;

    /**
     * Helper method to convert a list of entities to a DTO.
     *
     * @param userId The user id to use for the response dto.
     * @param addressEntities The list of entities to convert.
     * @return UserAddressesResponseDto
     */
    public static UserAddressesResponseDto from(String userId, List<UserAddress> addressEntities) {
        List<UserAddressResponseDto> addressDtos = new ArrayList<>();
        if (addressEntities != null) {
            addressDtos = addressEntities.stream()
                .map(UserAddressResponseDto::fromEntity)
                .toList();
        }

        return UserAddressesResponseDto.builder()
                .userId(userId)
                .addresses(addressDtos)
                .build();
    }

}
