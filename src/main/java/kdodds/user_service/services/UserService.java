package kdodds.user_service.services;

import kdodds.user_service.models.User;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserService {

    /**
     * User data for a given user id. Will throw an exception for a null or missing user id.
     *
     * @param userId Unique user id of the user.
     * @return User
     * @throws Exception Thrown when invalid user id is provided.
     */
    public User getUserByUserId(String userId) throws Exception {
        if (userId == null || userId.isEmpty()) {
            // TODO: throw typed exception
            throw new Exception("No user for null or empty user id");
        }

        return User.builder()
                   .id(userId)
                   .username("warlordbattlehound4848")
                   .email("somewhere@someplace.com")
                   .status("active")
                   .passwordHash("password")
                   .updatedAt(Instant.now())
                   .createdAt(Instant.now())
                   .build();
    }

}
