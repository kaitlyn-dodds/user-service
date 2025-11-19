package kdodds.userservice.repositories.specifications;

import jakarta.persistence.criteria.Predicate;
import kdodds.userservice.entities.User;
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
    public static Specification<User> build(String username) {
        return (root, query, cb) -> {
            // list of filtering conditions to apply to the query
            List<Predicate> predicates = new ArrayList<>();

            if (username != null && !username.isEmpty()) {
                // add username predicate (WHERE username is LIKE '%username%')
                predicates.add(cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%"));
            }

            // add more filtering conditions here...

            // return the combined predicate
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
