package kdodds.userservice.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class CreateUserRequestDto {

    @JsonProperty(value = "username", required = true)
    private String username;

    @JsonProperty(value = "email", required = true)
    private String email;

    @JsonProperty(value = "first_name", required = true)
    private String firstName;

    @JsonProperty(value = "last_name", required = true)
    private String lastName;

    @JsonProperty(value = "phone_number", required = true)
    private String phoneNumber;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    @JsonProperty(value = "address", required = true)
    private UserAddressRequestDto address;

}
