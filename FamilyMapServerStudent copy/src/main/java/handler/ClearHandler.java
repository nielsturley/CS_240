package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import result.ClearResult;
import service.ClearService;

import java.io.*;
import java.net.HttpURLConnection;

public class ClearHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            //check for 'post' request method
            if (!(exchange.getRequestMethod().equalsIgnoreCase("post"))) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                ClearResult result = new ClearResult("Error: Invalid request", false);
                createResponseBody(result, exchange);
            }
            else {
                InputStream reqBody = exchange.getRequestBody();
                String reqData = readString(reqBody);

                //display the input on system
                System.out.println("\nClear request:");
                System.out.println("url: " + exchange.getRequestURI().toString());
                System.out.println("request body:");
                System.out.println(reqData);

                //begin ClearService
                ClearService service = new ClearService();
                ClearResult result = service.clear();

                if (result.isSuccess()) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    createResponseBody(result, exchange);
                    System.out.println("Successfully completed Clear request");
                }
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                    createResponseBody(result, exchange);
                }
        }
    } catch (IOException e) {
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
        e.printStackTrace();
        ClearResult result = new ClearResult("Error: Internal server error", false);
        createResponseBody(result, exchange);
    }

}


    private void createResponseBody(ClearResult result, HttpExchange exchange) throws IOException {
        //display the output on system
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

