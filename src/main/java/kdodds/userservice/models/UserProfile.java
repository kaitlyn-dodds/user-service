package kdodds.userservice.models;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UserProfile {

    private String userId;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String profileImageUrl;

    private Instant createdAt;

    private Instant updatedAt;

}
