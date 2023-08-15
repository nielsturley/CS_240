package result;

/**
 * LoadResult class returned to LoadHandler functions.
 */
public class LoadResult {
    private String message;
    private boolean success;

    /**
     * Creates a new LoadResponse. Since the error response and the success response
     * have the same parameters, boolean success is passed in to determine whether an
     * error occurred.
     *
     * @param message description of the successful load or of the error.
     * @param success true if no error occurred, false if an error occurred.
     */
    public LoadResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "{\n" +
                "\t\"message\":\"" + message + "\"\n" +
                "\t\"success\":" + success + "\n" +
                "}";
    }
}
