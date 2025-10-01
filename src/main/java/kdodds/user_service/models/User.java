package kdodds.user_service.models;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
public class User {

    private String id;

    private String username;

    private String email;

    private String passwordHash;

    private String status; // active, inactive, banned, pending, deleted, etc.

    private Instant createdAt;

    private Instant updatedAt;

}
