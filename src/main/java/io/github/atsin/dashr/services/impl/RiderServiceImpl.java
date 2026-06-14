package io.github.atsin.dashr.services.impl;

import io.github.atsin.dashr.dto.DriverDto;
import io.github.atsin.dashr.dto.RideDto;
import io.github.atsin.dashr.dto.RideRequestDto;
import io.github.atsin.dashr.dto.RiderDto;
import io.github.atsin.dashr.entities.*;
import io.github.atsin.dashr.entities.enums.RideRequestStatus;
import io.github.atsin.dashr.entities.enums.RideStatus;
import io.github.atsin.dashr.exceptions.ResourceNotFoundException;
import io.github.atsin.dashr.repositories.RideRequestRepository;
import io.github.atsin.dashr.repositories.RiderRepository;
import io.github.atsin.dashr.services.DriverService;
import io.github.atsin.dashr.services.RatingService;
import io.github.atsin.dashr.services.RideService;
import io.github.atsin.dashr.services.RiderService;
import io.github.atsin.dashr.strategies.RideStrategyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiderServiceImpl implements RiderService {

    private final ModelMapper modelMapper;
    private final RideStrategyManager rideStrategyManager;
    private final RideRequestRepository rideRequestRepository;
    private final RiderRepository riderRepository;
    private final RideService rideService;
    private final DriverService driverService;
//    private final PageableHandlerMethodArgumentResolver pageableResolver;
    private final RatingService ratingService;

    @Override
    @Transactional
    public RideRequestDto requestRide(RideRequestDto rideRequestDto) {
        Rider rider = getCurrentRider();

        RideRequest rideRequest = modelMapper.map(rideRequestDto, RideRequest.class);
        rideRequest.setRideRequestStatus(RideRequestStatus.PENDING);
        rideRequest.setRider(rider);

        Double fare = rideStrategyManager.rideFareCalculationStrategy().calculateFare(rideRequest);
        rideRequest.setFare(fare);

        RideRequest savedRideRequest  = rideRequestRepository.save(rideRequest);

        List<Driver> drivers = rideStrategyManager.driverMatchingStrategy(rider.getRating()).findMatchingDriver(rideRequest);

        // TODO : Send Notification to all the drivers about the ride request

        return modelMapper.map(savedRideRequest, RideRequestDto.class);
    }

    @Override
    public RideDto cancelRide(Long rideId) {

        Rider rider = getCurrentRider();  // getCurrentRider() is currently hardcoded I will change it later
        Ride ride = rideService.getRideById(rideId);

        //checking if the rider has belongs to this ride or not
        if(!rider.equals(ride.getRider())) {
            throw new RuntimeException("Rider does not belong to this ride");
        }

        // checking if ride status is confirmed or not
        // because Rider is only able to cancel the ride only if the ride is confirmed
        if(!ride.getRideStatus().equals(RideStatus.CONFIRMED)){
            throw new RuntimeException("Ride is not confirmed yet. Ride Status: " + ride.getRideStatus());
        }

        //updating the ride status to cancelled
        Ride savedRide = rideService.updateRideStatus(ride, RideStatus.CANCELLED);

        //updating the driver availability from Not-Available to available
        driverService.updateDriverAvailability(ride.getDriver(), true);

        //mapping the updated Ride to RideDto
        return modelMapper.map(savedRide, RideDto.class);

    }

    @Override
    public DriverDto rateDriver(Long rideId, Integer rating) {
        Ride ride = rideService.getRideById(rideId);
        Rider rider = getCurrentRider();

        //Checking if driver belongs to this ride or not
        if(!rider.equals(ride.getDriver())) {
            throw new RuntimeException("Rider does not belong to this ride");
        }
        //Checking if RideStatus is ENDED or not because
        //Driver and Rider only able to rate the ride when it is ENDED
        //If it is not ENDED then throw an exception
        if(!ride.getRideStatus().equals(RideStatus.ENDED)){
            throw new RuntimeException("Ride status is not ENDED hence cannot rate, status: "+ride.getRideStatus());
        }

        return ratingService.rateDriver(ride, rating);
    }

    @Override
    public RiderDto getMyProfile() {
        //get Current Rider and map it to RiderDto
        Rider rider = getCurrentRider();
        return modelMapper.map(rider, RiderDto.class);
    }

    @Override
    public Page<RideDto> getAllMyRides(Pageable pageable) {
        // getting current Rider
        Rider rider = getCurrentRider();

        //getting all the rides of this rider
        return rideService.getAllRidesOfRider(rider, pageable).map(
                ride -> modelMapper.map(ride, RideDto.class)
        );
    }

    @Override
    public Rider createNewRider(User user) {
        Rider rider = Rider.builder()
                .user(user)
                .rating(0.0)
                .build();
        return riderRepository.save(rider);
    }

    @Override
    public Rider getCurrentRider() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return riderRepository.findByUser(user).orElseThrow(
                () -> new ResourceNotFoundException("Rider not associated with current user with id: " + user.getId())
        );
    }
}
