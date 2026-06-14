package io.github.atsin.dashr.services;

import io.github.atsin.dashr.dto.DriverDto;
import io.github.atsin.dashr.dto.RideDto;
import io.github.atsin.dashr.dto.RiderDto;
import io.github.atsin.dashr.entities.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DriverService {

    RideDto acceptRide(Long rideRequestId);
    RideDto cancelRide(Long rideId);
    RideDto startRide(Long rideId, String otp);
    RideDto endRide(Long rideId);
    RiderDto rateRider(Long rideId, Integer rating);

    DriverDto getMyProfile();
    Page<RideDto> getAllMyRides(Pageable pageRequest);
    Driver getCurrentDriver();

    Driver updateDriverAvailability(Driver driver, boolean available);

    Driver createNewDriver(Driver driver);

}
