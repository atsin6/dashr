package io.github.atsin.dashr.services.impl;

import io.github.atsin.dashr.entities.RideRequest;
import io.github.atsin.dashr.exceptions.ResourceNotFoundException;
import io.github.atsin.dashr.repositories.RideRequestRepository;
import io.github.atsin.dashr.services.RideRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideRequestServiceImpl implements RideRequestService {

    private final RideRequestRepository rideRequestRepository;

    @Override
    public RideRequest findRideRequestById(Long rideRequestId) {
        return rideRequestRepository.findById((rideRequestId))
                .orElseThrow(()-> new ResourceNotFoundException("RideRequest not found with id: "+rideRequestId));

    }

    @Override
    public void update(RideRequest rideRequest) {
        rideRequestRepository.findById(rideRequest.getId())
                .orElseThrow(()-> new ResourceNotFoundException("RideRequest not found with id: "+rideRequest.getId()));

        rideRequestRepository.save(rideRequest);

    }
}
