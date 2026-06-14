package io.github.atsin.dashr.repositories;

import io.github.atsin.dashr.entities.Rider;
import io.github.atsin.dashr.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RiderRepository extends JpaRepository<Rider, Long> {
    Optional<Rider> findByUser(User user);
}
