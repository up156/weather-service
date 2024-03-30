package org.example.client;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeatherClient {

    private final static String key = "";
    private final static String WEATHER_NOW = "https://api.openweathermap.org/data/2.5/weather?q=&lang=ru&units=metric&appid=" + key;
    private final static String WEATHER_FORECAST = "https://api.openweathermap.org/data/2.5/forecast?q=&lang=ru&units=metric&appid=" + key;

    public WeatherClient() {
    }

    public List<String> getWeather(String city, Integer days) {

        try {
            List<String> weatherReply = getWeatherReply(city);
            if (weatherReply.isEmpty()) {
                return Collections.singletonList("нет информации для этого города");
            }

            String now = weatherReply.get(0);
            String forecast = weatherReply.get(1);
            JSONParser parser = new JSONParser();
            org.json.simple.JSONObject object = (org.json.simple.JSONObject) parser.parse(now);
            org.json.simple.JSONObject main = (org.json.simple.JSONObject) object.get("main");
            org.json.simple.JSONObject sys = (org.json.simple.JSONObject) object.get("sys");
            org.json.simple.JSONObject weather = (JSONObject) ((JSONArray) object.get("weather")).get(0);
            List<String> result = new ArrayList<>(List.of("\uD83E\uDD99 Температура  сейчас: "
                            + (Math.round(Double.parseDouble(main.get("temp").toString()) * 10) / 10.0) + "°",
                    getEmoji(weather.get("icon").toString()) + " На улице " + weather.get("description"),
                    "☀" + "☀" + "☀" + " Восход солнца: " + convertEpochToStringLocalTime(sys.get("sunrise").toString()),
                    "\uD83C\uDF1D\uD83C\uDF1D\uD83C\uDF1D " +
                            "Заход солнца: " + convertEpochToStringLocalTime(sys.get("sunset").toString()), "", "",
                    "⚡⚡", "", ""));

            org.json.simple.JSONObject objectForecast = (org.json.simple.JSONObject) parser.parse(forecast);
            org.json.simple.JSONArray list = (org.json.simple.JSONArray) objectForecast.get("list");
            LocalDate temp = LocalDate.now();
            result.add("Прогноз на: " + temp.format(DateTimeFormatter.ofPattern("dd/MM")));
            temp = temp.plus(1, ChronoUnit.DAYS);

            for (int j = 0; j < 40; j++) {

                org.json.simple.JSONObject weatherForecast = (JSONObject) ((JSONArray) ((JSONObject) list.get(j)).get("weather")).get(0);
                Long dt = (Long) ((JSONObject) list.get(j)).get("dt");
                LocalDateTime dateOfForecast = convertEpochToLocalTime(dt.toString());
                if (dateOfForecast.isBefore(LocalDateTime.now()
                        .plus(days, ChronoUnit.DAYS).plus(4, ChronoUnit.HOURS))) {
                    if (dateOfForecast.toLocalDate().isEqual(ChronoLocalDate.from(temp))) {
                        result.add("");
                        result.add("Прогноз на: " + dateOfForecast.format(DateTimeFormatter.ofPattern("dd/MM")));
                        result.add("");
                        temp = temp.plus(1, ChronoUnit.DAYS);
                    }
                    org.json.simple.JSONObject mainForecast = (JSONObject) ((JSONObject) list.get(j)).get("main");
                    String string = dateOfForecast.format(DateTimeFormatter.ofPattern("HH:mm dd/MM")) + " - " +
                            getEmoji(weatherForecast.get("icon").toString()) + " " +
                            (Math.round(Float.parseFloat(mainForecast.get("temp").toString()) * 10)) / 10.0 +
                            "°" + " " + weatherForecast.get("description") + "\r\n";
                    result.add(string);
                }
            }
            System.out.println(result);
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
            return Collections.singletonList("что-то сломалось");
        }
    }

    private List<String> getWeatherReply(String city) {

        List<String> replies = new ArrayList<>();
        try {
            city = URLEncoder.encode(city, StandardCharsets.UTF_8);
            List<URL> list = List.of((new URL(new StringBuilder(WEATHER_NOW).insert(50, city).toString())),
                    new URL(new StringBuilder(WEATHER_FORECAST).insert(51, city).toString()));
            list.forEach(url -> {
                try {
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("GET");
                    con.setRequestProperty("Accept-Language", "en-US,ru;q=0.5");
                    con.setConnectTimeout(5000);
                    con.setReadTimeout(5000);
                    con.setDoOutput(true);

                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                    String inputLine;
                    StringBuilder reply = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        reply.append(inputLine);
                    }
                    in.close();
                    replies.add(reply.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return replies;
    }

    private LocalDateTime convertEpochToLocalTime(String epoch) {

        return Instant.ofEpochSecond(Long.parseLong(epoch))
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

    }

    private String convertEpochToStringLocalTime(String epoch) {

        return Instant.ofEpochSecond(Long.parseLong(epoch))
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("HH:mm dd/MM"));

    }

    private String getEmoji(String icon) {
        return switch (icon) {
            case "01d" -> "☀";
            case "01n" -> "\uD83C\uDF1A";
            case "02d" -> "\uD83C\uDF24";
            case "02n", "03d", "03n", "04d", "04n" -> "☁";
            case "09d" -> "\uD83C\uDF26";
            case "09n", "10d", "10n" -> "\uD83C\uDF28";
            case "11d", "11n" -> "\uD83C\uDF29";
            case "13d", "13n" -> "❄";
            case "50d", "50n" -> "\uD83C\uDF2B";
            default -> "";
        };
    }
}
