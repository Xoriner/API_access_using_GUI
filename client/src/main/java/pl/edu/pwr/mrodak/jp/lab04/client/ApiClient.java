package pl.edu.pwr.mrodak.jp.lab04.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.edu.pwr.mrodak.jp.lab04.client.models.AvgLivingSpace;
import pl.edu.pwr.mrodak.jp.lab04.client.models.AvgLivingSpaceResponse;
import pl.edu.pwr.mrodak.jp.lab04.client.models.Province;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiClient {
    // Creating HttpClient
    HttpClient httpClient = HttpClient.newHttpClient();

    public List<Province> getProvinces() {
        List<Province> provinces = new ArrayList<>();
        try {
            // building the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api-dbw.stat.gov.pl/api/1.1.0/variable/variable-section-position?id-przekroj=2&lang=pl"))
                    .GET()
                    .build();

            // sending the request and getting the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse JSON response using Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            provinces = objectMapper.readValue(response.body(), new TypeReference<List<Province>>() {});

        } catch (Exception e) {
            // exception handling
            e.printStackTrace();
            System.err.println("Wystąpił błąd podczas wykonywania żądania HTTP.");
        }

        return provinces;
    }

    public List<AvgLivingSpace> getAvgLivingSpace(int year) {
        List<AvgLivingSpace> avgLivingSpaces = new ArrayList<>();
        try {
            // building the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api-dbw.stat.gov.pl/api/1.1.0/variable/variable-data-section?id-zmienna=541&id-przekroj=2&id-rok=%d&id-okres=282&lang=pl".formatted(year)))
                    .GET()
                    .build();

            // sending the request and getting the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse JSON response using Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            AvgLivingSpaceResponse avgLivingSpaceResponse = objectMapper.readValue(response.body(), AvgLivingSpaceResponse.class);
            avgLivingSpaces = avgLivingSpaceResponse.getData();

        } catch (Exception e) {
            // exception handling
            e.printStackTrace();
            System.err.println("Wystąpił błąd podczas wykonywania żądania HTTP.");
        }

        return avgLivingSpaces;
    }

    public List<AvgLivingSpace> getAvgLivingSpace(Integer fromYear, Integer toYear) {
        if (fromYear > toYear) {
            throw new IllegalArgumentException("fromYear cannot be greater than toYear");
        }

        List<AvgLivingSpace> avgLivingSpaces = new ArrayList<>();
        for (int year = fromYear; year <= toYear; year++) {
            avgLivingSpaces.addAll(getAvgLivingSpace(year));
            try {
                Thread.sleep(200); //5 request per second
            } catch (InterruptedException  e)   {
                e.printStackTrace();
            }
        }

        return avgLivingSpaces;
    }
}
