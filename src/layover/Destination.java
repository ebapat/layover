package layover;

/** One place on the bucket list, reached through a gateway airport. */
public class Destination {
    public final String name;
    public final String airportCode;
    public final int interest; // 1-10, how much I want to go

    public Destination(String name, String airportCode, int interest) {
        this.name = name;
        this.airportCode = airportCode;
        this.interest = interest;
    }
}
