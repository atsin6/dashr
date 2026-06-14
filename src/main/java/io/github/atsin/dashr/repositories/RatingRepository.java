package io.github.atsin.dashr.repositories;

import io.github.atsin.dashr.entities.Driver;
import io.github.atsin.dashr.entities.Rating;
import io.github.atsin.dashr.entities.Ride;
import io.github.atsin.dashr.entities.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRider(Rider rider);
    List<Rating> findByDriver(Driver driver);

    Optional<Rating> findByRide(Ride ride);
}
