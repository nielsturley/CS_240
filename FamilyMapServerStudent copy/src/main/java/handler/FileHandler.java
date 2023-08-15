package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        //error404 path definition, used if the input file path is invalid
        Path error404 = new File("web/HTML/404.html").toPath();
        try {
            //check for 'get' request method
            if (!(exchange.getRequestMethod().equalsIgnoreCase("get"))) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                OutputStream respBody = exchange.getResponseBody();
                Files.copy(error404, respBody);
                respBody.close();
            } else {
                InputStream reqBody = exchange.getRequestBody();
                String reqData = readString(reqBody);

                //display input on system
                System.out.println("\nFile request:");
                System.out.println("url: " + exchange.getRequestURI().toString());
                System.out.println("request body:");
                System.out.println(reqData);

                //if urlpath is '/' or null, set as default '/index.html'
                String urlPath = exchange.getRequestURI().toString();
                if (Objects.equals(urlPath, "/") || urlPath == null) {
                    urlPath = "/index.html";
                }

                //begin find file
                String filePath = "web" + urlPath;
                File file = new File(filePath);
                if (file.exists()) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    OutputStream respBody = exchange.getResponseBody();
                    Files.copy(file.toPath(), respBody);
                    respBody.close();
                }
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                    OutputStream respBody = exchange.getResponseBody();
                    Files.copy(error404, respBody);
                    respBody.close();
                }
                System.out.println("Successfully completed File request");
            }
        } catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            e.printStackTrace();
            OutputStream respBody = exchange.getResponseBody();
            Files.copy(error404, respBody);
            respBody.close();
        }
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
}
