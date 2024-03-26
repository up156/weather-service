package org.example.service;

import jakarta.persistence.EntityManager;
import org.example.entity.Weather;

import static jakarta.persistence.Persistence.createEntityManagerFactory;


public class WeatherService {

    public void save(Weather weather) {
        EntityManager entityManager = createEntityManagerFactory("Weather").createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(weather);
        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
