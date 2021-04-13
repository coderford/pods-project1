package com.CabCompany.RideService;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Scanner;

public class RequestSender {
    public static String getHTTPResponse(String url, List<String> paramNames, List<String> paramVals) {
        // Encode parameters
        try {
            for (int i = 0; i < paramNames.size(); i++) {
                paramVals.set(i, URLEncoder.encode(paramVals.get(i), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            System.out.println("ERROR: Unsupported encoding format!");
            return "<ERROR>";
        }

        // Build query
        String query = "";
        for(int i = 0; i < paramNames.size(); i++) {
            query += paramNames.get(i)+"="+paramVals.get(i);
            if(i < paramNames.size() - 1) query += "&";
        }

        // Get response
        String responseString = "<ERROR>";

        URLConnection connection;
        try {
            connection = new URL(url + "?" + query).openConnection();
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            InputStream response = connection.getInputStream();
            Scanner scanner = new Scanner(response);
            responseString = scanner.useDelimiter("\\A").next();
            scanner.close();
        } catch (Exception e) {
            System.out.println("ERROR: GET from URL "+url+" failed");
            return "<ERROR>";
        }

        return responseString;
    }
}
