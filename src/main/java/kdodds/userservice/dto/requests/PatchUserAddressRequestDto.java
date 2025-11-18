package kdodds.userservice.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Data
public class PatchUserAddressRequestDto {

    @JsonProperty("address_type")
    private String addressType; // billing, shipping, home, etc.

    @JsonProperty(value = "address_line_1")
    private String addressLine1;

    @JsonProperty("address_line_2")
    private String addressLine2;

    @JsonProperty(value = "city")
    private String city;

    @JsonProperty(value = "state")
    private String state;

    @JsonProperty(value = "zip_code")
    private String zipCode;

    @JsonProperty(value = "country")
    private String country;

}
