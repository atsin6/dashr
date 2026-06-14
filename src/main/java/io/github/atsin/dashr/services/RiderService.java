package io.github.atsin.dashr.services;

import io.github.atsin.dashr.dto.DriverDto;
import io.github.atsin.dashr.dto.RideDto;
import io.github.atsin.dashr.dto.RideRequestDto;
import io.github.atsin.dashr.dto.RiderDto;
import io.github.atsin.dashr.entities.Rider;
import io.github.atsin.dashr.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RiderService {

    RideRequestDto requestRide(RideRequestDto rideRequestDto);
    RideDto cancelRide(Long rideId);
    DriverDto rateDriver(Long rideId, Integer rating);

    RiderDto getMyProfile();
    Page<RideDto> getAllMyRides(Pageable pageRequest);


    //Internal Methods
    Rider createNewRider(User user);

    Rider  getCurrentRider();
}
