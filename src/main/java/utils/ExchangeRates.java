package utils;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ExchangeRates {

    private static BigDecimal dolar;

    public static void requestDolarOficial() {
        String apiUrl = "https://dolarapi.com/v1/dolares/oficial";
        try (HttpClient client = HttpClient
                .newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build()
        ) {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                JSONObject jsonObject = new JSONObject(response.body());
                dolar = jsonObject.getBigDecimal("venta");
            } catch (Exception e) {
                System.err.println("No se pudo obtener el tipo de cambio");
                dolar = null;
            }
        }
    }

    public static BigDecimal getDolar() {
        return dolar;
    }

    public static void setDolar(BigDecimal value) {
        dolar = value;
    }
}
