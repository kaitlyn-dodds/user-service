package kdodds.userservice.repositories;

import kdodds.userservice.entities.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
