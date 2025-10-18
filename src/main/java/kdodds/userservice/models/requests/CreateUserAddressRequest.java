package kdodds.userservice.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Data
public class CreateUserAddressRequest {

    @JsonProperty(value = "user_id", required = true)
    private String userId;

    @JsonProperty(value = "address", required = true)
    private UserAddressRequest address;

}
