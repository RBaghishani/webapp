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
    Optional<User> findById(Long id);
    // Query method to find patients by firstname and lastname
    List<User> findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(String firstname, String lastname);

    List<User> findByRole(Role role);
}
