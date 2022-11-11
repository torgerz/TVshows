package com.example.demo;

import org.bson.json.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Hente informasjon fra API og opprette Java Object som kan eksporteres som kan lastes opp på DB.
 *
 */
public class ApiHandler {
    private static Logger LOGGER = Logger.getLogger("InfoLogging");

    public static String getJSONFromApi(String name)  {
        String info = "";
        try{
            System.setProperty("https.protocols","TLSv1,TLSv1.1,TLSv1.2");
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tvmaze.com/singlesearch/shows?q=" + name + "&embed=episodes"))
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            info = response.body();

        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return info;

    }
    /**
     * Gets list of shows for recommendation.
     */
    public static String getBigShowList() {
        String info = "";
        try {
            System.setProperty("http.protocols", "TLSv1, TLSv1.1,TLSv1.2");
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tvmaze.com/shows?page=1"))
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            info = response.body();

        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return info;
    }

    public static String listThroughApi (ArrayList<String> shows) {
        StringBuilder resultString = new StringBuilder();
        for (String show : shows) {
            ApiBackOffStrategy backoff = new ApiBackOffStrategy();
            String msg = null;
            boolean flag = false;
            while (backoff.shouldRetry()) {
                msg = getJSONFromApi(show);
                if (msg != null) {
                    backoff.doNotRetry();
                    flag = true;
                    MongoDB.setShowEpisodes(msg,"TVdata");
                    break;
                } else {
                    LOGGER.info("** Retrying **");
                    backoff.errorOccurred();
                }

            }
            if (flag) {
                resultString.append( "Successful: " + show);
            } else {
                resultString.append( "Not Successful: " + show);
            }
            
        }
        return resultString.toString();
    }


}
