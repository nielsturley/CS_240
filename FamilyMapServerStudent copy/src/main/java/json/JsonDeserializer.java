package json;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JsonDeserializer {

    //used to parse locations from provided random locations
    public Locations parseLocations(File file) throws IOException {
        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            Gson gson = new Gson();
            return gson.fromJson(bufferedReader, Locations.class);
        }
    }

    //used to parse names from provided random names
    public Names parseNames(File file) throws IOException {
        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            Gson gson = new Gson();
            return gson.fromJson(bufferedReader, Names.class);
        }
    }
}
