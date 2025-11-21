package kdodds.userservice.repositories.specifications;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import kdodds.userservice.entities.User;
import kdodds.userservice.entities.UserProfile;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    /**
     * Builds a specification for a user with the given filters.
     *
     * @param username The username to filter on.
     * @return Specification<User>
     */
    public static Specification<User> build(
        String username,
        String email,
        String firstName,
        String lastName,
        String status
    ) {
        return (root, query, cb) -> {
            // list of filtering conditions to apply to the query
            List<Predicate> predicates = new ArrayList<>();

            Join<User, UserProfile> profile = root.join("userProfile");

            // username filter
            if (username != null && !username.isEmpty()) {
                // add username predicate (WHERE username is LIKE '%username%')
                predicates.add(cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%"));
            }

            // email filter
            if (email != null && !email.isEmpty()) {
                // add email predicate (WHERE email is LIKE '%email%')
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }

            // first name filter
            if (firstName != null && !firstName.isEmpty()) {
                // add first name predicate (WHERE first_name is LIKE '%first_name%')
                predicates.add(cb.like(cb.lower(profile.get("firstName")), "%" + firstName.toLowerCase() + "%"));
            }

            // last name filter
            if (lastName != null && !lastName.isEmpty()) {
                // add last name predicate (WHERE last_name is LIKE '%last_name%')
                predicates.add(cb.like(cb.lower(profile.get("lastName")), "%" + lastName.toLowerCase() + "%"));
            }

            // active filter
            if (status != null && !status.isEmpty()) {
                // add active predicate (WHERE status = active)
                predicates.add(cb.equal(root.get("status"), status.toUpperCase()));
            }

            // add more filtering conditions here...

            // return the combined predicate
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
