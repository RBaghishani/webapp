package it.unipd.webapp.repository;

import it.unipd.webapp.entity.User;
import it.unipd.webapp.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//    User findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndRole(Long id, Role role);
    // Query method to find patients by firstname and lastname
    List<User> findByRole(Role role);

    default List<User> findByNameOrLastNameOrRole(String name, String lastName, Role role) {
        if (name == null && lastName == null) {
            return findByRole(role);
        } else if (name == null) {
            return findByLastnameContainingIgnoreCaseAndRole(lastName, role);
        } else if (lastName == null) {
            return findByFirstnameContainingIgnoreCaseAndRole(name, role);
        } else {
            return findByFirstnameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndRole(name, lastName, role);
        }
    }

    List<User> findByFirstnameContainingIgnoreCaseAndRole(String name, Role role);

    List<User> findByLastnameContainingIgnoreCaseAndRole(String lastName, Role role);

    List<User> findByFirstnameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndRole(String name, String lastName, Role role);

}
