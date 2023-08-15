package json;

import java.util.Objects;

public class Location {
    private final String country;
    private final String city;
    private final float latitude;
    private final float longitude;

    public Location(String country, String city, float latitude, float longitude) {
        this.country = country;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return Float.compare(location.latitude, latitude) == 0 && Float.compare(location.longitude, longitude) == 0 && country.equals(location.country) && city.equals(location.city);
    }

}
