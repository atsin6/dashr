package io.github.atsin.dashr.dto;

import io.github.atsin.dashr.entities.Rider;
import io.github.atsin.dashr.entities.enums.PaymentMethod;
import io.github.atsin.dashr.entities.enums.RideRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestDto {

    private Long id;

    private PointDto pickUpLocation;
    private PointDto dropOffLocation;
    private PaymentMethod paymentMethod;

    private LocalDateTime requestedTime;

    private Rider rider;
    private RideRequestStatus rideRequestStatus;

    private Double fare;
}
