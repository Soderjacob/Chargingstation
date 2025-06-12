package com.chargingstation.demo.client;

import org.springframework.web.client.RestTemplate;

import java.sql.SQLOutput;

public class ChargingStationClient {
    private static final String BASE_URL = "http://localhost:5000";

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        // Hämta laddstationens info
        String stationInfo = restTemplate.getForObject(BASE_URL + "/info", String.class);
        System.out.println("Laddstationsinfo: " + stationInfo);

        // Skicka kommando för att starta laddning
        String response = restTemplate.postForObject(BASE_URL + "/charge", "{\"charging\":\"on\"}", String.class);
        System.out.println("Svar efter laddningstart: " + response);
    }
}
