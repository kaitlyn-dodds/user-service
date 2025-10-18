package kdodds.userservice.models;

import kdodds.userservice.models.responses.UserAddressResponse;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UserAddress {

    private String id;

    private String userId;

    private String addressType; // billing, shipping, home, etc.

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String zipCode;

    private String country;

    private Instant createdAt;

    private Instant updatedAt;

    /**
     * Convert a UserAddress to a UserAddressResponse.
     *
     * @param address The UserAddress to convert.
     * @return UserAddressResponse object.
     */
    public static UserAddressResponse convert(UserAddress address) {
        return UserAddressResponse.builder()
            .id(address.getId())
            .userId(address.getUserId())
            .addressType(address.getAddressType())
            .addressLine1(address.getAddressLine1())
            .city(address.getCity())
            .state(address.getState())
            .zipCode(address.getZipCode())
            .country(address.getCountry())
            .createdAt(address.getCreatedAt())
            .updatedAt(address.getUpdatedAt())
            .build();
    }

}
