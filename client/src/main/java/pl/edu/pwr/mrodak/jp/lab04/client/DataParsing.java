package pl.edu.pwr.mrodak.jp.lab04.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class DataParsing {
    public static void parseAndPrint(String jsonResponse) {
        try {
            // Tworzenie obiektu ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // Parsowanie JSON do drzewa (JsonNode)
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // Przechodzenie do tablicy "data"
            JsonNode dataArray = rootNode.get("data");

            // Sprawdzenie, czy "data" istnieje i jest tablicą
            if (dataArray != null && dataArray.isArray()) {
                List<String> results = new ArrayList<>();

                for (JsonNode row : dataArray) {
                    // Sprawdzanie warunku "id-pozycja-1" == 33617
                    if (row.get("id-pozycja-1").asInt() == 33617) {
                        // Pobieranie "id_daty" i "wartosc"
                        int idDaty = row.get("id-daty").asInt();
                        double wartosc = row.get("wartosc").asDouble();

                        // Dodawanie wyniku do listy
                        results.add("id_daty: " + idDaty + ", wartosc: " + wartosc);
                    }
                }

                // Wyświetlanie wyników
                results.forEach(System.out::println);
            } else {
                System.out.println("Brak danych w sekcji 'data'.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Wystąpił błąd podczas parsowania danych JSON.");
        }
    }
}
