package io.github.atsin.dashr.strategies.impl;

import io.github.atsin.dashr.entities.Driver;
import io.github.atsin.dashr.entities.RideRequest;
import io.github.atsin.dashr.repositories.DriverRepository;
import io.github.atsin.dashr.strategies.DriverMatchingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverMatchingNearestDriverStrategy implements DriverMatchingStrategy {

    private final DriverRepository driverRepository;

    @Override
    public List<Driver> findMatchingDriver(RideRequest rideRequest) {
        return driverRepository.findTenNearestDrivers(rideRequest.getPickUpLocation());
    }
}
