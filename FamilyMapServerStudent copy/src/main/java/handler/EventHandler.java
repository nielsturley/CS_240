package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import request.AllEventRequest;
import request.EventRequest;
import result.AllEventResult;
import result.EventResult;
import service.AllEventService;
import service.EventService;

import java.io.*;
import java.net.HttpURLConnection;

public class EventHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            //check for 'get' request method
            if (!(exchange.getRequestMethod().equalsIgnoreCase("get"))) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                EventResult result = new EventResult("Error: Invalid request");
                createResponseBody(result, exchange);
            } else {
                Headers reqHeaders = exchange.getRequestHeaders();

                //check for 'Authorization' key
                if (!(reqHeaders.containsKey("Authorization"))) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                    EventResult result = new EventResult("Error: Invalid request");
                    createResponseBody(result, exchange);
                } else {
                    String authToken = reqHeaders.getFirst("Authorization");
                    InputStream reqBody = exchange.getRequestBody();
                    String reqData = readString(reqBody);

                    //divide input urlPath
                    String urlPath = exchange.getRequestURI().toString();
                    String[] strings = urlPath.split("/", 3);

                    String eventID = null;
                    if (strings.length == 3) { //if there is a specified eventID
                        eventID = strings[2];
                    }

                    if (eventID != null) { //this is an EventRequest
                        //display input on system
                        System.out.println("\nEvent request:");
                        System.out.println("url: " + exchange.getRequestURI().toString());
                        System.out.println("request body:");
                        System.out.println(reqData);

                        //begin EventService
                        EventRequest request = new EventRequest(eventID, authToken);
                        EventService service = new EventService();
                        EventResult result = service.getEvent(request);

                        if (result.isSuccess()) {
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                            createResponseBody(result, exchange);
                            System.out.println("Successfully completed Event request");
                        } else {
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                            createResponseBody(result, exchange);
                        }
                    } else { //this is an AllEventRequest
                        //display input on system
                        System.out.println("\nAllEvent request:");
                        System.out.println("url: " + exchange.getRequestURI().toString());
                        System.out.println("request body:");
                        System.out.println(reqData);

                        //begin AllEventService
                        AllEventRequest request = new AllEventRequest(authToken);
                        AllEventService service = new AllEventService();
                        AllEventResult result = service.getAllEvents(request);

                        if (result.isSuccess()) {
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                            createResponseBody(result, exchange);
                            System.out.println("Successfully completed AllEvent request");
                        } else {
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                            createResponseBody(result, exchange);
                        }
                    }
                }
            }
        } catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            e.printStackTrace();
            EventResult result = new EventResult("Error: Internal server error");
            createResponseBody(result, exchange);
        }
    }

    //overloaded function for EventResult
    private void createResponseBody(EventResult result, HttpExchange exchange) throws IOException {
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

    //overloaded function for AllEventResult
    private void createResponseBody(AllEventResult result, HttpExchange exchange) throws IOException {
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


