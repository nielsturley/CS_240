package json;

import java.util.List;
import java.util.Random;

public class Names {
    private final List<String> data;

    public Names(List<String> names) { this.data = names; }

    public String pickRandomName() {
        Random rand = new Random();
        int randomIndex = rand.nextInt(data.size());
        return data.get(randomIndex);
    }
}
