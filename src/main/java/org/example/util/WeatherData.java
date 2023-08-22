package org.example.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.WeatherDataProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;


@Component
public class WeatherData {

    private final WeatherDataProperties.WeatherProperties weatherProperties;
    private final WeatherDataProperties.GeocodeProperties geocodeProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public WeatherData(WeatherDataProperties weatherDataProperties) {
        this.weatherProperties = weatherDataProperties.getWeather();
        this.geocodeProperties = weatherDataProperties.getGeocode();
    }


    public SendLocation getCoordinates(Message cityInfo) throws JsonProcessingException {


        ObjectMapper objectMapper = new ObjectMapper();
        String url = geocodeProperties.getUrl() + geocodeProperties.getKey() + "&geocode=" + cityInfo.getText() + "&format=json";
        JsonNode jsonNode = objectMapper.readTree(restTemplate.getForObject(url, String.class));

        String[] cords = jsonNode.path("response").path("GeoObjectCollection").path("featureMember")
                .get(0).path("GeoObject").path("Point").path("pos").asText().split(" ");
        return new SendLocation(cityInfo.getChatId().toString(), Double.parseDouble(cords[1]), Double.parseDouble(cords[0]));
    }

    private String getCoordinates(String cityName) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        String url = geocodeProperties.getUrl() + geocodeProperties.getKey() + "&geocode=" + cityName + "&format=json";
        JsonNode jsonNode = objectMapper.readTree(restTemplate.getForObject(url, String.class));

        return jsonNode.path("response").path("GeoObjectCollection").path("featureMember")
                .get(0).path("GeoObject").path("Point").path("pos").asText();
    }

    public SendMessage getWeatherInfo(Message incomingMessage) throws JsonProcessingException {


        String[] cords = getCoordinates(incomingMessage.getText()).split(" ");

        ObjectMapper objectMapper = new ObjectMapper();

        String url = weatherProperties.getUrl() + "lon=" + cords[0] + "&lat=" + cords[1] + "&extra=true";


        HttpHeaders headers = new HttpHeaders();
        headers.add(weatherProperties.getHead(), weatherProperties.getKey());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);


        JsonNode jsonNode = objectMapper.readTree(restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class).getBody());


        String temp = jsonNode.path("fact").path("temp").toString();
        String feelsLike = jsonNode.path("fact").path("feels_like").toString();
        String condition = jsonNode.path("fact").path("condition").toString();
        String windSpeed = jsonNode.path("fact").path("wind_speed").toString();

        return new SendMessage(incomingMessage.getChatId().toString(), "Температура: " + temp + "°C" + "\n"
                + "Ощущается как: " + feelsLike + "°C" + "\n"
                + "Скорость ветра: " + windSpeed + "м/с" + "\n"
                + "На улице сейчас: " + parseCondition(condition));

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
