package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import request.FillRequest;
import result.FillResult;
import service.FillService;

import java.io.*;
import java.net.HttpURLConnection;

public class FillHandler implements HttpHandler {
    private static final int GENERATION_DEFAULT = 4;

    @Override
    public void handle(HttpExchange exchange) throws IOException, NumberFormatException {
        try {
            //check for 'post' request method
            if (!(exchange.getRequestMethod().equalsIgnoreCase("post"))) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                FillResult result = new FillResult("Error: Invalid request", false);
                createResponseBody(result, exchange);
            }
            else {
                InputStream reqBody = exchange.getRequestBody();
                String reqData = readString(reqBody);

                //display input on system
                System.out.println("\nFill request:");
                System.out.println("url: " + exchange.getRequestURI().toString());
                System.out.println("request body:");
                System.out.println(reqData);

                //divide input urlpath
                String urlPath = exchange.getRequestURI().toString();
                String[] strings = urlPath.split("/", 4);
                String username = strings[2];
                int generations = GENERATION_DEFAULT;
                if (strings.length == 4) { //if there is a specified generation number
                    generations = Integer.parseInt(strings[3]); //parse the int. will throw error if it is not an int.
                }

                //begin FillService
                FillRequest request = new FillRequest(username, generations);
                FillService service = new FillService();
                FillResult result = service.fill(request);

                if (result.isSuccess()) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    createResponseBody(result, exchange);
                    System.out.println("Successfully completed Fill request");
                }
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                    createResponseBody(result, exchange);
                }
            }
        } catch (NumberFormatException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            FillResult result = new FillResult("Error: Invalid generation parameter", false);
            createResponseBody(result, exchange);
        } catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            e.printStackTrace();
            FillResult result = new FillResult("Error: Internal server error", false);
            createResponseBody(result, exchange);
        }

    }

    private void createResponseBody(FillResult result, HttpExchange exchange) throws IOException {
        //display output on system
        System.out.println("response body:");
        System.out.println(result.toString());

        //create and close response body
        OutputStream resBody = exchange.getResponseBody();
        Gson gson1 = new GsonBuilder().create();
        String resultString = gson1.toJson(result);
        writeString(resultString, resBody);
        resBody.close();
    }

    private String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}
