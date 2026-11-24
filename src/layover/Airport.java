package layover;

/** One airport from airports.dat. Only the fields Layover needs. */
public class Airport {
    public final String iataCode;
    public final String name;
    public final String city;
    public final String country;
    public final double latitude;
    public final double longitude;

    public Airport(String iataCode, String name, String city, String country,
                   double latitude, double longitude) {
        this.iataCode = iataCode;
        this.name = name;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return iataCode + " - " + name + " (" + city + ", " + country + ")";
    }
}
