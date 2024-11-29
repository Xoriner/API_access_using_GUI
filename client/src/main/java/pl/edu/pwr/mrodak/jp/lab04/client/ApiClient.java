package pl.edu.pwr.mrodak.jp.lab04.client;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public class ApiClient {
    public void printApi() {
        try {
            // Creating HttpClient
            HttpClient httpClient = HttpClient.newHttpClient();

            // building the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api-dbw.stat.gov.pl/api/1.1.0/variable/variable-data-section?id-zmienna=541&id-przekroj=2&id-rok=2019&id-okres=282&lang=pl"))
                    .GET()
                    .build();

            // sending the request and getting the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // raw json answer
            System.out.println("Raw JSON response: " + response.body());

            // Parsing the Json answer
            DataParsing.parseAndPrint(response.body());

        } catch (Exception e) {
            // exception handling
            e.printStackTrace();
            System.err.println("Wystąpił błąd podczas wykonywania żądania HTTP.");
        }
    }
}
