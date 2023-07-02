package org.example.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@PropertySource(value = {"classpath:application.properties"})
@Component
public class WeatherData {

    @Value("${yandex.weather.api.head}")
    private String keyHead;
    @Value("${yandex.weather.api.key}")
    private String keyWeather;
    @Value("${yandex.geocode.api.key}")
    private String keyGeoCode;
    @Value("${yandex.weather.api.url}")
    private String urlWeather;
    @Value("${yandex.geocode.api.url}")
    private String urlGeoCode;

    private final RestTemplate restTemplate = new RestTemplate();


    private String getCoordinates(String cityName) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String url = urlGeoCode + keyGeoCode + "&geocode=" + cityName + "&format=json";

        JsonNode jsonNode = objectMapper.readTree(restTemplate.getForObject(url, String.class));

        String pos = jsonNode.path("response").path("GeoObjectCollection").path("featureMember")
                .get(0).path("GeoObject").path("Point").path("pos").asText();

        return pos;
    }

    public String getWeatherInfo(String cityName) throws JsonProcessingException {
        String[] cords = getCoordinates(cityName).split(" ");

        ObjectMapper objectMapper = new ObjectMapper();

        String url = urlWeather + "lon=" + cords[0] + "&lat=" + cords[1] + "&extra=true";


        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);


        headers.add(keyHead, keyWeather);
        headers.setContentType(MediaType.APPLICATION_JSON);


        JsonNode jsonNode = objectMapper.readTree(restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class).getBody());

        String temp = jsonNode.path("fact").path("temp").toString();
        String feelsLike = jsonNode.path("fact").path("feels_like").toString();
        String condition = jsonNode.path("fact").path("condition").toString();
        String windSpeed = jsonNode.path("fact").path("wind_speed").toString();

        return "Температура: " + temp + "°C" + "\n"
                + "Ощущается как: " + feelsLike + "°C" + "\n"
                + "Скорость ветра: " + windSpeed + "м/с" + "\n"
                + "На улице сейчас: " + parseCondition(condition);

    }

    private String parseCondition(String condition) {
        HashMap<String, String> dict = new HashMap<>();
        dict.put("\"clear\"", "ясно");
        dict.put("\"partly-cloudy\"", "малооблачно");
        dict.put("\"cloudy\"", "облачно с прояснениями");
        dict.put("\"overcast\"", "пасмурно");
        dict.put("\"light-rain\"", "небольшой дождь");
        dict.put("\"rain\"", "дождь");
        dict.put("\"heavy-rain\"", "сильный дождь");
        dict.put("\"showers\"", "ливень");
        dict.put("\"wet-snow\"", "дождь со снегом");
        dict.put("\"light-snow\"", "небольшой снег");
        dict.put("\"snow\"", "снег");
        dict.put("\"snow-showers\"", "снегопад");
        dict.put("\"hail\"", "град");
        dict.put("\"thunderstorm\"", "гроза");
        dict.put("\"thunderstorm-with-rain\"", "дождь с грозой");
        dict.put("\"thunderstorm-with-hail\"", "гроза с градом");

        return dict.get(condition);
    }

}
