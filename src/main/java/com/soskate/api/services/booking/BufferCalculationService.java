package com.soskate.api.services.booking;

import com.soskate.api.entities.SpotEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BufferCalculationService {

    private static final double PARIS_AVERAGE_SPEED_KMH = 12.0;
    private static final int BASE_BUFFER_MINUTES = 10;
    private static final int ROUND_TO_MINUTES = 15;
    private static final int MIN_BUFFER = 15;
    private static final int MAX_BUFFER = 90;
    private static final int EARTH_RADIUS_KM = 6371;

    public int calculateBuffer(SpotEntity from, SpotEntity to) {
        if (from.getId().equals(to.getId())) {
            return 0;
        }

        double distanceKm = calculateHaversineDistance(
                from.getLatitude(),
                from.getLongitude(),
                to.getLatitude(),
                to.getLongitude()
        );

        int travelMinutes = (int) Math.ceil((distanceKm / PARIS_AVERAGE_SPEED_KMH) * 60);
        int totalMinutes = travelMinutes + BASE_BUFFER_MINUTES;
        int roundedMinutes = ((totalMinutes + ROUND_TO_MINUTES - 1) / ROUND_TO_MINUTES) * ROUND_TO_MINUTES;

        return Math.max(MIN_BUFFER, Math.min(MAX_BUFFER, roundedMinutes));
    }

    private double calculateHaversineDistance(
            BigDecimal fromLatitude,
            BigDecimal fromLongitude,
            BigDecimal toLatitude,
            BigDecimal toLongitude
    ) {
        double fromLatRadians = Math.toRadians(fromLatitude.doubleValue());
        double toLatRadians = Math.toRadians(toLatitude.doubleValue());

        double latitudeDifference = Math.toRadians(
                toLatitude.subtract(fromLatitude).doubleValue()
        );
        double longitudeDifference = Math.toRadians(
                toLongitude.subtract(fromLongitude).doubleValue()
        );

        double haversineFormula = Math.sin(latitudeDifference / 2) * Math.sin(latitudeDifference / 2)
                + Math.cos(fromLatRadians) * Math.cos(toLatRadians)
                * Math.sin(longitudeDifference / 2) * Math.sin(longitudeDifference / 2);

        double angularDistance = 2 * Math.atan2(
                Math.sqrt(haversineFormula),
                Math.sqrt(1 - haversineFormula)
        );

        return EARTH_RADIUS_KM * angularDistance;
    }
}