package result;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Person;

import java.util.List;

/**
 * AllPersonResult class returned to AllPersonHandler functions.
 */
public class AllPersonResult {
    private List<Person> data;
    private boolean success;
    private String message;

    /**
     * Creates a new AllPersonResult when an error occurs (error response).
     *
     * @param message description of the error.
     */
    public AllPersonResult(String message) {
        this.message = message;
        success = false;
    }

    /**
     * Creates a new AllPersonResult when no error occurs (success response).
     *
     * @param data an array of all Person objects associated with the user.
     */
    public AllPersonResult(List<Person> data) {
        this.data = data;
        success = true;
    }

    public List<Person> getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setData(List<Person> data) {
        this.data = data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        if (success) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(this);
        }
        else {
            return "{\n" +
                    "\t\"message\":\"" + message + "\"\n" +
                    "\t\"success\":" + false + "\n" +
                    "}";
        }
    }
}
