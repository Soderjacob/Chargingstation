package com.chargingstation.demo.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

//import java.net.http.HttpHeaders;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;

@Service
public class ChargingStationService {
    private static final String BASE_URL = "http://localhost:5000";

    private static final String API_URL = "https://www.elprisetjustnu.se/api/v1/prices/2025/06-06_SE3.json";
    private RestTemplate restTemplate = new RestTemplate();

    public ChargingStationService() {
        this.restTemplate = new RestTemplate();
    }

    public JsonNode getElectricityPrices()  {
        try {
            String jsonResponse = restTemplate.getForObject(API_URL, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(jsonResponse);
        }   catch (Exception e) {
            throw new RuntimeException("Fel vid hämtning av priser: " + e.getMessage());
        }
    }

    public JsonNode getHouseholdEnergyConsumption()  {
        try {
            String jsonResponse = restTemplate.getForObject(BASE_URL + "/baseload", String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(jsonResponse);
        }   catch (Exception e) {
            throw new RuntimeException("Fel vid hämtning av hushållets energiförbrukning: " + e.getMessage());
        }
    }

    public String startCharging()   {
        return sendChargingCommand("on");
    }

    public String stopCharging()   {
        return sendChargingCommand("off");
    }

    public String sendChargingCommand(String command) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("{\"charging\":\"" + command + "\"}", headers);

        return restTemplate.postForObject(BASE_URL + "/charge", request, String.class);
    }

    public JsonNode getBatteryPercentage()  {
        try {
            String jsonResponse = restTemplate.getForObject(BASE_URL + "/charge", String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(jsonResponse);
        } catch (Exception e) {
            throw new RuntimeException("Fel vid hämtning av batteriets laddning: " + e.getMessage());
        }
    }

    // Automatisk laddning: startas om kriterierna uppfylls
    public String manageCharging()   {
        JsonNode consumptionData = getHouseholdEnergyConsumption();
        JsonNode priceData = getElectricityPrices();
        JsonNode batteryData = getBatteryPercentage();

        int currentBattery = batteryData.asInt();
        double lowestConsumption = findLowestConsumption(consumptionData);

        if (currentBattery < 80 && lowestConsumption < 11) {
            return startCharging();
        } else {
            return stopCharging();
        }
    }

    private double findLowestConsumption(JsonNode consumptionData) {
        double lowest = Double.MAX_VALUE;
        for (JsonNode value : consumptionData) {
            double consumption = value.asDouble();
            if (consumption < lowest) {
                lowest = consumption;
            }
        }
        return lowest;
    }

    public String dischargeBattery() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String jsonPayload = "{\"discharging\":\"on\"}";

            HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers); // Skickar tom JSON

            String jsonResponse = restTemplate.postForObject("http://127.0.0.1:5000/discharge", request, String.class);
            return "Batteri urladdat till 20%: " + jsonResponse;
        } catch (Exception e) {
            return "Fel vid urladdning: " + e.getMessage();
        }
    }



    private int findOptimalChargingHour(JsonNode priceData, JsonNode consumptionData) {
        int bestHour = -1;
        double lowestPrice = Double.MAX_VALUE;

        for (int hour = 0; hour < priceData.size(); hour++) {
            double price = priceData.get(hour).asDouble();
            double consumption = consumptionData.get(hour).asDouble();

            if (consumption < 11 && price < lowestPrice) {
                lowestPrice = price;
                bestHour = hour;
            }
        } return bestHour;
    }

    public String generateChargingSchedule() {
        JsonNode priceData = getElectricityPrices();
        JsonNode consumptionData = getHouseholdEnergyConsumption();

        StringBuilder table = new StringBuilder();
        table.append(String.format("%-10s %-15s %-15s %s%n", "Timme", "Elpris (öre)", "Förbrukning (kW)", "Laddning"));

        for (int hour = 0; hour < priceData.size(); hour++) {
            double price = priceData.get(hour).asDouble();
            double consumption = consumptionData.get(hour).asDouble();
            String chargingStatus = (consumption < 11 && price == findLowestPrice(priceData)) ? "**LADDAR**" : "";

            table.append(String.format("%-10d %-15.2f %-15.2f %s%n", hour, price, consumption, chargingStatus));
        }
        return table.toString();
    }

    // **Smart laddning baserat på pris och hushållsförbrukning**
    public String optimizeCharging()   {
        JsonNode priceData = getElectricityPrices();
        JsonNode consumptionData = getHouseholdEnergyConsumption();
        JsonNode batteryData = getBatteryPercentage();
        int currentBattery = batteryData.asInt();

        String chargingState = "";

        // Hitta bästa timme baserat på pris och förbrukning
        int optimalHour = findOptimalChargingHour(priceData, consumptionData);

        // Starta laddning om en bra timme hittas och batteriet inte är fullt
        if (optimalHour != -1 && currentBattery < 80) {
            //return startCharging();
            chargingState = "on";
        }

        // Stoppa laddning om batteriet redan är fullt
        if (currentBattery >= 80) {
            System.out.println("HÄÄÄÄÄÄÄRRRRR");
            // return stopCharging();
            chargingState = "off";
        }

        // Ladda batteriet om det är under 20%
        if (currentBattery < 20) {
            //return startCharging();
            chargingState = "on";
        }

        return chargingState;
    }

    private double findLowestPrice(JsonNode priceData) {
        double lowest = Double.MAX_VALUE;
        for (JsonNode value : priceData) {
            double price = value.asDouble();
            if (price < lowest) {
                lowest = price;
            }
        }
        return lowest;
    }

}
