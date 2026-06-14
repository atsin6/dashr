package io.github.atsin.dashr.repositories;

import io.github.atsin.dashr.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //
    Optional<User> findByEmail(String email);

}
