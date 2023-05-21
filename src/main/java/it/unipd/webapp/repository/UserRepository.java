package it.unipd.webapp.repository;

import it.unipd.webapp.entity.Patient;
import it.unipd.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//    User findByUsername(String username);
    Optional<User> findByEmail(String email);
}
