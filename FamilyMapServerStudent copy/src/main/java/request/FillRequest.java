package request;

/**
 * FillRequest class. Passed into FillService functions.
 */
public class FillRequest {
    private String username;
    private final int generations;

    /**
     * Creates a new FillRequest.
     *
     * @param username the user's username.
     * @param generations the number of generations to be filled. Default is 4.
     */
    public FillRequest(String username, int generations) {
        this.username = username;
        this.generations = generations;
    }

    public String getUsername() {
        return username;
    }

    public int getGenerations() {
        return generations;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
