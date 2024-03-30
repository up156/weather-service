package org.example.service;

import jakarta.persistence.EntityManager;
import org.example.client.WeatherClient;
import org.example.entity.Weather;
import org.example.jdbc.JDBCClient;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.Persistence.createEntityManagerFactory;


public class WeatherService {

    private final JDBCClient JDBCClient;

    private final WeatherClient weatherClient;

    private final EntityManager entityManager;

    public WeatherService() {

        this.JDBCClient = new JDBCClient();
        this.weatherClient = new WeatherClient();
        this.entityManager = createEntityManagerFactory("Weather").createEntityManager();
    }

    public List<String> getWeather(String city, Integer days) {
        Weather result = queryForWeather().stream()
                .filter(e -> e.getCity().equals(city) && e.getDays().equals(days))
                .findFirst().orElse(null);

        List<String> reply;
        if (result == null) {
            reply = weatherClient.getWeather(city, days);
            saveWeather(city, days, reply);
        } else {
            reply = new ArrayList<>();
                reply.add(result.getCurrentWeather());
                reply.add(result.getForecast());
        }
        return reply;
    }

    public void saveWeather(String city, Integer days, List<String> reply) {
        saveToDBWithHibernate(city, days, reply);
    }

    private void saveToDBWithJDBC(String city, Integer days, List<String> reply) {

        JDBCClient.saveResponse(city, days, reply.get(0), reply.get(1));
    }

    private void saveToDBWithHibernate(String city, Integer days, List<String> reply) {

        Weather weather = new Weather();
        weather.setCity(city);
        weather.setDateTime(Instant.now());
        weather.setDays(days);
        weather.setCurrentWeather(reply.get(0));
        weather.setForecast(reply.get(1));
        save(weather);

    }

    public List<Weather> queryForWeather() {
        Instant time = Instant.now().minus(Duration.ofMinutes(25));
        List<?> weather = entityManager.createQuery(
                "SELECT weather from Weather weather where weather.dateTime >:time")
                .setParameter("time", time)
                .getResultList();

        return weather.stream().map(o -> (Weather) o).toList();
    }

    private void save(Weather weather) {
        EntityManager entityManager = createEntityManagerFactory("Weather").createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(weather);
        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
