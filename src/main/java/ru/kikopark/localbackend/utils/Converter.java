package ru.kikopark.localbackend.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class Converter {
    static public String convertMinutesToHours(int minutes) {
        if (minutes < 0) {
            return "Некорректное значение";
        }
        if (minutes < 60) {
            return minutes + " минут";
        }
        int hours = minutes / 60;
        if (hours == 10) {
            return "День";
        }
        return hours + " час" + (hours > 1 ? "а" : "");
    }
    static public  <T> Optional<T> jsonToObject(String json, Class<T> classObject) {
        ObjectMapper mapper = new ObjectMapper();
        Optional<T> result = Optional.empty();
        try {
            T mappedObject = mapper.readValue(json, classObject);
            result = Optional.of(mappedObject);
        } catch (JsonProcessingException e) {
            System.err.println("Converter.jsonToObject Error: " + e.getMessage());
        }
        return result;
    }
}
