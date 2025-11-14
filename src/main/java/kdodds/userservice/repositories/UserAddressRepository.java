package kdodds.userservice.repositories;

import jakarta.transaction.Transactional;
import kdodds.userservice.entities.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {

    /**
     * Find all addresses for a given user.
     *
     * @param userId The user id to use to find the addresses.
     * @return List of UserAddress objects.
     */
    Optional<List<UserAddress>> findAddressesByUserId(UUID userId);

    /**
     * Delete an address by user id and address id.
     *
     * @param userId The user id of the owning user.
     * @param addressId The address id to delete.
     * @return The number of rows affected.
     */
    @Transactional
    @Modifying
    @Query(value = """
        DELETE FROM user_addresses
        WHERE user_id = :userId AND id = :addressId
        
        """, nativeQuery = true)
    int deleteAddressById(
        @Param("userId") UUID userId,
        @Param("addressId") UUID addressId
    );
}
