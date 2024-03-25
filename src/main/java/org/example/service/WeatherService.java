package org.example.service;

import org.example.entity.Weather;
import org.example.util.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class WeatherService {

    public void save(Weather weather) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(weather);
        transaction.commit();
        session.flush();
        session.close();
    }
}
