package result;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Event;

import java.util.List;

/**
 * AllEventResult class returned to AllEventHandler functions.
 */
public class AllEventResult {
    private List<Event> data;
    private boolean success;
    private String message;

    /**
     * Creates a new AllPersonResult when an error occurs (error response).
     *
     * @param message description of the error.
     */
    public AllEventResult(String message) {
        this.message = message;
        success = false;
    }

    /**
     * Creates a new AllEventResult when no error occurs (success response).
     *
     * @param data an array of all Event objects associated with the user.
     */
    public AllEventResult(List<Event> data) {
        this.data = data;
        success = true;
    }

    public List<Event> getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setData(List<Event> data) {
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
