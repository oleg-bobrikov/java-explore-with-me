package ru.practicum.ewm.util;

import static java.lang.Math.cos;

public class Geo {
    public static double distance(double lat1, double lon1, double lat2, double lon2, Unit unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0; // If the coordinates are the same, the distance is zero.
        } else {
            double theta = lon1 - lon2;

            // Calculate the Haversine formula
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) +
                    cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60; // Convert to nautical miles

            // Convert the result based on the selected unit
            switch (unit) {
                case METER:
                    return dist * 1852; // Convert to meters
                case KILOMETER:
                    return dist * 1.852; // Convert to kilometers
                case STATUE_MILE:
                    return dist * 1.15078; // Convert to statute miles
                case NAUTICAL_MILE:
                default:
                    return dist; // Return the distance in nautical miles by default.
            }
        }
    }

    public enum Unit {
        NAUTICAL_MILE,
        METER,
        KILOMETER,
        STATUE_MILE
    }
}
