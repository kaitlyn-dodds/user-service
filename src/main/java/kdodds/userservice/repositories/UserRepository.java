package kdodds.userservice.repositories;

import jakarta.transaction.Transactional;
import kdodds.userservice.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Create a new user and profile and address.
     *
     * @param username The username to use for the user.
     * @param email The email to use for the user.
     * @param passwordHash The password hash to use for the user.
     * @param firstName The first name to use for the user profile.
     * @param lastName The last name to use for the user profile.
     * @param phoneNumber The phone number to use for the user profile.
     * @param profileImageUrl The profile image url to use for the user profile.
     * @param addressType The address type to use for the user address.
     * @param addressLine1 The address line 1 to use for the user address.
     * @param addressLine2 The address line 2 to use for the user address.
     * @param city The city to use for the user address.
     * @param state The state to use for the user address.
     * @param zipCode The zip code to use for the user address.
     * @param country The country to use for the user address.
     * @return The user id of the newly created user.
     */
    @Transactional
    @Query(value = """
        WITH created_user AS (
            INSERT INTO users (username, email, password_hash, status, created_at, updated_at)
            VALUES (:username, :email, :passwordHash, 'ACTIVE', NOW(), NOW())
            RETURNING id
        ),
        created_profile AS (
            INSERT INTO user_profiles (user_id, first_name, last_name, phone_number, profile_image_url, created_at, updated_at)
            SELECT id, :firstName, :lastName, :phoneNumber, :profileImageUrl, NOW(), NOW()
            FROM created_user
            RETURNING user_id
        )
        INSERT INTO user_addresses (user_id, address_type, address_line_1, address_line_2, city, state, zip_code, country, created_at, updated_at)
        SELECT user_id, :addressType, :addressLine1, :addressLine2, :city, :state, :zipCode, :country, NOW(), NOW()
        FROM created_profile
        RETURNING user_id;
        """, nativeQuery = true)
    UUID createUserAndProfileAndAddress(
        @Param("username") String username,
        @Param("email") String email,
        @Param("passwordHash") String passwordHash,
        @Param("firstName") String firstName,
        @Param("lastName") String lastName,
        @Param("phoneNumber") String phoneNumber,
        @Param("profileImageUrl") String profileImageUrl,
        @Param("addressType") String addressType,
        @Param("addressLine1") String addressLine1,
        @Param("addressLine2") String addressLine2,
        @Param("city") String city,
        @Param("state") String state,
        @Param("zipCode") String zipCode,
        @Param("country") String country
    );

    /**
     * Create a new user and profile.
     *
     * @param username The username to use for the user.
     * @param email The email to use for the user.
     * @param passwordHash The password hash to use for the user.
     * @param firstName The first name to use for the user profile.
     * @param lastName The last name to use for the user profile.
     * @param phoneNumber The phone number to use for the user profile.
     * @param profileImageUrl The profile image url to use for the user profile.
     * @return The user id of the newly created user.
     */
    @Transactional
    @Query(value = """
        WITH created_user AS (
            INSERT INTO users (username, email, password_hash, status, created_at, updated_at)
            VALUES (:username, :email, :passwordHash, 'ACTIVE', NOW(), NOW())
            RETURNING id
        )
        INSERT INTO user_profiles (user_id, first_name, last_name, phone_number, profile_image_url, created_at, updated_at)
        SELECT id, :firstName, :lastName, :phoneNumber, :profileImageUrl, NOW(), NOW()
        FROM created_user
        RETURNING user_id;
        """, nativeQuery = true)
    UUID createUserAndProfile(
        @Param("username") String username,
        @Param("email") String email,
        @Param("passwordHash") String passwordHash,
        @Param("firstName") String firstName,
        @Param("lastName") String lastName,
        @Param("phoneNumber") String phoneNumber,
        @Param("profileImageUrl") String profileImageUrl
    );


}
