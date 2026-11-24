package layover;

/** Distance between two points on Earth using the haversine formula. */
public class Distance {

    // Mean radius of the Earth in miles. Miles because the cost formula is priced per mile.
    private static final double EARTH_RADIUS_MILES = 3958.8;

    /**
     * Great-circle distance in miles. A straight line on a map is wrong for
     * flights because the Earth is curved, so we measure along its surface.
     */
    public static double miles(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        return EARTH_RADIUS_MILES * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public static double miles(Airport a, Airport b) {
        return miles(a.latitude, a.longitude, b.latitude, b.longitude);
    }
}
