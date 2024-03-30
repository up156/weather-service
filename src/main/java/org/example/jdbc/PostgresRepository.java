package org.example.jdbc;


import java.sql.*;
import java.time.LocalDate;

public class PostgresRepository {

    private final String url = "jdbc:postgresql://localhost:5432/app_db";

    private final String username = "postgres";

    private final String password = "postgres";

    private final String sql = "INSERT INTO WEATHER.WEATHER (DATETIME, CITY, DAYS, CURRENT_WEATHER, FORECAST) VALUES (?, ?, ?, ?, ?)";

    public void saveResponse(String city, int days, String currentWeather, String forecast){
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDate(1, Date.valueOf(LocalDate.now()));
            statement.setString(2, city);
            statement.setInt(3, days);
            statement.setString(4, currentWeather);
            statement.setString(5, forecast);
            statement.executeUpdate();
            statement.close();
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}
