package io.github.atsin.dashr.services;


import io.github.atsin.dashr.dto.DriverDto;
import io.github.atsin.dashr.dto.RideDto;
import io.github.atsin.dashr.dto.RiderDto;
import io.github.atsin.dashr.entities.Driver;
import io.github.atsin.dashr.entities.Ride;
import io.github.atsin.dashr.entities.Rider;

public interface RatingService {
    DriverDto rateDriver(Ride ride, Integer rating);
    RiderDto rateRider(Ride ride, Integer rating);

    void createNewRating(Ride ride);

}
