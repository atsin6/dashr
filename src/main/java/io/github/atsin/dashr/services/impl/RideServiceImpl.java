package io.github.atsin.dashr.services.impl;

import io.github.atsin.dashr.entities.Driver;
import io.github.atsin.dashr.entities.Ride;
import io.github.atsin.dashr.entities.RideRequest;
import io.github.atsin.dashr.entities.Rider;
import io.github.atsin.dashr.entities.enums.RideRequestStatus;
import io.github.atsin.dashr.entities.enums.RideStatus;
import io.github.atsin.dashr.exceptions.ResourceNotFoundException;
import io.github.atsin.dashr.repositories.RideRepository;
import io.github.atsin.dashr.services.RideRequestService;
import io.github.atsin.dashr.services.RideService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RideRequestService rideRequestService;
    private final ModelMapper modelMapper;

    @Override
    public Ride getRideById(Long RideId) {
        return rideRepository.findById(RideId).orElseThrow(()->
                new ResourceNotFoundException("Ride with id " + RideId + " not found"));
    }

    @Override
    public Ride createNewRide(RideRequest rideRequest, Driver driver) {
        rideRequest.setRideRequestStatus(RideRequestStatus.CONFIRMED);
        Ride ride = modelMapper.map(rideRequest, Ride.class);
        ride.setRideStatus(RideStatus.CONFIRMED);
        ride.setDriver(driver);
        ride.setOtp(generateRandomOtp());
        ride.setId(null);

        rideRequestService.update(rideRequest);
        return rideRepository.save(ride);
    }

    @Override
    public Ride updateRideStatus(Ride ride, RideStatus rideStatus) {
        ride.setRideStatus(rideStatus);
        return rideRepository.save(ride);

    }

    @Override
    public Page<Ride> getAllRidesOfRider(Rider rider, Pageable pageable) {
        return rideRepository.findByRider(rider, pageable);
//        return null;
    }

    @Override
    public Page<Ride> getAllRidesOfDriver(Driver driver, Pageable pageable) {
        return rideRepository.findByDriver(driver, pageable);
    }

    private String generateRandomOtp(){
        Random random = new Random();
        int otpInt = random.nextInt(10000); //0 to 9999
        return String.format("%04d", otpInt);
    }
}
