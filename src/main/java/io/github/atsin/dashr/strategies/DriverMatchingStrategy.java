package io.github.atsin.dashr.strategies;

import io.github.atsin.dashr.entities.Driver;
import io.github.atsin.dashr.entities.RideRequest;

import java.util.List;

public interface DriverMatchingStrategy {
    List<Driver> findMatchingDriver(RideRequest rideRequest);
}
