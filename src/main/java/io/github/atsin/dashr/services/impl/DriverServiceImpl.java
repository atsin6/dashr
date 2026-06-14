package io.github.atsin.dashr.services.impl;

import io.github.atsin.dashr.dto.DriverDto;
import io.github.atsin.dashr.dto.RideDto;
import io.github.atsin.dashr.dto.RiderDto;
import io.github.atsin.dashr.entities.*;
import io.github.atsin.dashr.entities.enums.RideRequestStatus;
import io.github.atsin.dashr.entities.enums.RideStatus;
import io.github.atsin.dashr.exceptions.ResourceNotFoundException;
import io.github.atsin.dashr.repositories.DriverRepository;
import io.github.atsin.dashr.services.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final RideRequestService rideRequestService;
    private final RideService rideService;
    private final ModelMapper modelMapper;
    private final PaymentService paymentService;
    private final RatingService ratingService;

    @Override
    @Transactional
    public RideDto acceptRide(Long rideRequestId) {

        RideRequest rideRequest = rideRequestService.findRideRequestById(rideRequestId);

        if(!rideRequest.getRideRequestStatus().equals(RideRequestStatus.PENDING)) {
            throw new RuntimeException("RideRequest cannot be accepted, status is "+rideRequest.getRideRequestStatus());
        }

        Driver currentDriver = getCurrentDriver();

        if(!currentDriver.getAvailable()){
            throw new RuntimeException("Driver cannot accept ride due to unavailability");
        }

        Driver savedDriver = updateDriverAvailability(currentDriver, false);

        Ride ride = rideService.createNewRide(rideRequest, savedDriver);
        return modelMapper.map(ride, RideDto.class);
    }



    @Override
    public RideDto cancelRide(Long rideId) {

        // Getting the current ride entity using the rideId
        Ride ride = rideService.getRideById(rideId);
        // getCurrentDriver() is currently hardcoded I will change it later
        Driver driver = getCurrentDriver();

        //checking if the driver has belongs to this ride or not
        if(!driver.equals(ride.getDriver())) {
            throw new RuntimeException("Driver does not belong to this ride");
        }

        // checking if ride status is confirmed or not
        // because Driver is only able to cancel the ride only if the ride is confirmed
        if(!ride.getRideStatus().equals(RideStatus.CONFIRMED)) {
            throw new RuntimeException("Ride is not confirmed yet. Ride Status: " + ride.getRideStatus());
        }

        //updating the ride status to cancelled
        Ride savedRide = rideService.updateRideStatus(ride, RideStatus.CANCELLED);

        //updating the driver availability from Not-Available to available
        updateDriverAvailability(driver, true);

        //mapping the updated Ride to RideDto
        return modelMapper.map(savedRide, RideDto.class);
    }



    @Override
    public RideDto startRide(Long rideId, String otp) {
        //getting the ride by rideId
        Ride ride = rideService.getRideById(rideId);
        // getting currentDriver
        Driver driver = getCurrentDriver();

        //Checking if driver belongs to this ride or not
        if(!driver.equals(ride.getDriver())) {
            throw new RuntimeException("Driver does not belong to this ride");
        }

        //Checking if RideStatus is confirmed or not because
        //Driver only able to start the ride when it is confirmed
        //If it is not confirmed then throws an exception
        if(!ride.getRideStatus().equals(RideStatus.CONFIRMED)){
            throw new RuntimeException("Ride status is not CONFIRMED hence cannot be started, status: "+ride.getRideStatus());

        }

        //Checking if otp is valid or not
        if(!otp.equals(ride.getOtp())) {
            throw new RuntimeException("OTP "+otp+" does not match");
        }

        //set the ride start time
        ride.setStartedAt(LocalDateTime.now());

        //update the ride status and saving it
        //this method update and save the ride
        Ride savedRide = rideService.updateRideStatus(ride, RideStatus.ONGOING);

        paymentService.creatNewPayment(savedRide);
        ratingService.createNewRating(savedRide);

        return modelMapper.map(savedRide, RideDto.class);
    }

    @Override
    public RideDto endRide(Long rideId) {
        //getting the ride by rideId
        Ride ride = rideService.getRideById(rideId);
        // getting currentDriver
        Driver driver = getCurrentDriver();

        //Checking if driver belongs to this ride or not
        if(!driver.equals(ride.getDriver())) {
            throw new RuntimeException("Driver does not belong to this ride");
        }

        //Checking if RideStatus is ongoing or not because
        //Driver only able to end the ride when it is ongoing
        //If it is not ongoing then throws an exception
        if(!ride.getRideStatus().equals(RideStatus.ONGOING)){
            throw new RuntimeException("Ride status is not ONGOING hence cannot be ended, status: "+ride.getRideStatus());

        }

        //checking if driver is at the dropOffLocation or not
//        if(!ride.getDropOffLocation().equals(driver.getCurrentLocation())){
//            throw new RuntimeException(
//                    "You cannot end the ride before reaching to destination. DropOffLocation: " + ride.getDropOffLocation());
//        }

        //setting end time
        ride.setEndedAt(LocalDateTime.now());

        Ride savedRide = rideService.updateRideStatus(ride, RideStatus.ENDED);
        updateDriverAvailability(driver, true);

        paymentService.processPayment(savedRide);
        return modelMapper.map(savedRide, RideDto.class);
    }

    @Override
    public RiderDto rateRider(Long rideId, Integer rating) {
        Ride ride = rideService.getRideById(rideId);
        Driver driver = getCurrentDriver();

        //Checking if driver belongs to this ride or not
        if(!driver.equals(ride.getDriver())) {
            throw new RuntimeException("Driver does not belong to this ride");
        }
        //Checking if RideStatus is ENDED or not because
        //Driver and Rider only able to rate the ride when it is ENDED
        //If it is not ENDED then throw an exception
        if(!ride.getRideStatus().equals(RideStatus.ENDED)){
            throw new RuntimeException("Ride status is not ENDED hence cannot rate, status: "+ride.getRideStatus());
        }

        return ratingService.rateRider(ride, rating);
    }

    @Override
    public DriverDto getMyProfile() {
        Driver driver = getCurrentDriver();
        return modelMapper.map(driver, DriverDto.class);
    }

    @Override
    public Page<RideDto> getAllMyRides(Pageable pageable) {
        Driver currentDriver = getCurrentDriver();
        return rideService.getAllRidesOfDriver(currentDriver, pageable).map(
                ride -> modelMapper.map(ride, RideDto.class)
        );
    }

    @Override
    public Driver getCurrentDriver(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return driverRepository.findByUser(user).orElseThrow(
                ()->new ResourceNotFoundException("Driver not associated with current user with id "+user.getId())
        );
    }

    @Override
    public Driver updateDriverAvailability(Driver driver, boolean available) {
        driver.setAvailable(available);
        return driverRepository.save(driver);
    }

    @Override
    public Driver createNewDriver(Driver driver) {
        return driverRepository.save(driver);
    }

}
