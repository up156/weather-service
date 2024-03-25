package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "weather", schema = "weather")
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "datetime")
    private Instant dateTime;

    @Column(name = "city")
    private String city;

    @Column(name = "current_weather")
    private String currentWeather;

    @Column(name = "forecast")
    private String forecast;

    @Column(name = "days")
    private Integer days;

}
