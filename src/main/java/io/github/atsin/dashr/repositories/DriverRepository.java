package io.github.atsin.dashr.repositories;

import io.github.atsin.dashr.entities.Driver;
import io.github.atsin.dashr.entities.User;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//ST_Distance(point1, point2) - Used to calculate distance between 2 points
//ST_DWithin(point1, point2, 10000) - Used to check if distance between 2 points is less than given distance

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {


    @Query(value = "SELECT d.*, ST_Distance(d.current_location, :pickUpLocation) AS distance " +
            "FROM driver d " +
            "WHERE d.available = true AND ST_DWithin(d.current_location, :pickUpLocation, 10000) " +
            "ORDER BY distance LIMIT 10", nativeQuery = true
    )
    List<Driver> findTenNearestDrivers(Point pickUpLocation);

    @Query(value = "SELECT d.* " +
            "FROM driver d " +
            "WHERE d.available = true AND st_dwithin(d.current_location, :pickUpLocation, 5000) "+
            "ORDER BY d.rating DESC "+
            "LIMIT 10 ", nativeQuery = true
    )
    List<Driver> findTenNearbyTopRatedDrivers(Point pickUpLocation);

    Optional<Driver> findByUser(User user);
}



