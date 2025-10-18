package kdodds.userservice.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class UserAddressRequest {

    @JsonProperty("address_type")
    private String addressType; // billing, shipping, home, etc.

    @JsonProperty(value = "address_line_1", required = true)
    private String addressLine1;

    @JsonProperty("address_line_2")
    private String addressLine2;

    @JsonProperty(value = "city", required = true)
    private String city;

    @JsonProperty(value = "state", required = true)
    private String state;

    @JsonProperty(value = "zip_code", required = true)
    private String zipCode;

    @JsonProperty(value = "country", required = true)
    private String country;

}
