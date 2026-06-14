package io.github.atsin.dashr.services;

import io.github.atsin.dashr.entities.RideRequest;

public interface RideRequestService {
    RideRequest findRideRequestById(Long rideRequestId);

    void update(RideRequest rideRequest);
}
