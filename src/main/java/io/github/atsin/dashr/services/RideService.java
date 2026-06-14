package io.github.atsin.dashr.services;

import io.github.atsin.dashr.entities.Driver;
import io.github.atsin.dashr.entities.Ride;
import io.github.atsin.dashr.entities.RideRequest;
import io.github.atsin.dashr.entities.Rider;
import io.github.atsin.dashr.entities.enums.RideStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface RideService {
    Ride getRideById(Long RideId);

    Ride createNewRide(RideRequest rideRequest, Driver driver);

    Ride updateRideStatus(Ride ride, RideStatus rideStatus);

    Page<Ride> getAllRidesOfRider(Rider rider, Pageable pageRequest);

    Page<Ride> getAllRidesOfDriver(Driver driver, Pageable pageRequest);

}
