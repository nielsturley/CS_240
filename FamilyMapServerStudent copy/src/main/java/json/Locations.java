package json;

import java.util.List;
import java.util.Random;

public class Locations {

    private final List<Location> data;

    public Locations(List<Location> locations) {
        this.data = locations;
    }

    public Location pickRandomLocation() {
        Random rand = new Random();
        int randomIndex = rand.nextInt(data.size());
        return data.get(randomIndex);
    }

}
