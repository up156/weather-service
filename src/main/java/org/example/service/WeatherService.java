package org.example.service;

import jakarta.persistence.EntityManager;
import org.example.client.WeatherClient;
import org.example.entity.Weather;
import org.example.jdbc.JDBCClient;

import java.time.Instant;
import java.util.List;

import static jakarta.persistence.Persistence.createEntityManagerFactory;


public class WeatherService {

    private final JDBCClient JDBCClient;

    private final WeatherClient weatherClient;

    public WeatherService() {
        this.JDBCClient = new JDBCClient();
        this.weatherClient = new WeatherClient();
    }

    public List<String> getWeather(String city, Integer days) {
        return weatherClient.getWeather(city, days);
    }

    public void saveWeather(String city, Integer days, List<String> reply) {
        saveToDBWithJDBC(city, days, reply);
    }

    private void saveToDBWithJDBC(String city, Integer days, List<String> reply) {

        JDBCClient.saveResponse(city, days, reply.subList(0, 5).toString().trim(),
                reply.subList(7, reply.size()).toString().trim());
    }

    private void saveToDBWithHibernate(String city, Integer days, List<String> reply) {

        Weather weather = new Weather();
        weather.setCity(city);
        weather.setDateTime(Instant.now());
        weather.setDays(days);
        weather.setCurrentWeather(reply.subList(0, 5).toString().trim());
        weather.setForecast(reply.subList(7, reply.size()).toString().trim());
        save(weather);

    }

    private void save(Weather weather) {
        EntityManager entityManager = createEntityManagerFactory("Weather").createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(weather);
        entityManager.getTransaction().commit();
        entityManager.close();
    }


}
