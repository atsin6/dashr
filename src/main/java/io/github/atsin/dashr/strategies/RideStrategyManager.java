package io.github.atsin.dashr.strategies;


import io.github.atsin.dashr.strategies.impl.DriverMatchingHighestRatedDriverStrategy;
import io.github.atsin.dashr.strategies.impl.DriverMatchingNearestDriverStrategy;
import io.github.atsin.dashr.strategies.impl.RideFareDefaultFareCalculationStrategy;
import io.github.atsin.dashr.strategies.impl.RideFareSurgePricingFareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class RideStrategyManager {

    private final DriverMatchingHighestRatedDriverStrategy highestRatedDriverStrategy;
    private final DriverMatchingNearestDriverStrategy nearestDriverStrategy;
    private final RideFareSurgePricingFareCalculationStrategy surgePricingFareCalculationStrategy;
    private final RideFareDefaultFareCalculationStrategy defaultFareCalculationStrategy;

    public DriverMatchingStrategy driverMatchingStrategy(double riderRating){
        if(riderRating >= 4.8){
            return highestRatedDriverStrategy;
        }
        return nearestDriverStrategy;
    }

    public RideFareCalculationStrategy rideFareCalculationStrategy(){

        //This is the time, where server is located we have to replace it with user time zone
        //we have to take zoneId as the input from the user in this function
        //and then pass it inside the LocalTime.of()

        // 6 PM to 9 PM is Surge Time
        LocalTime surgeStartTime = LocalTime.of(18, 0);
        LocalTime surgeEndTime = LocalTime.of(21, 0);
        LocalTime currentTime = LocalTime.now();
        boolean isSurgeTime = currentTime.isAfter(surgeStartTime) && currentTime.isBefore(surgeEndTime);

        if(isSurgeTime){
            return surgePricingFareCalculationStrategy;
        }
        return defaultFareCalculationStrategy;
    }
}
