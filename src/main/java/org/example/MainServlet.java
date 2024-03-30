package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.service.WeatherService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/weather")
@Slf4j
public class MainServlet extends HttpServlet {

    private final WeatherService weatherService;

    public MainServlet() {
        this.weatherService = new WeatherService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        log.info("working with request {}, {}", req.getRequestURL(), req.getMethod());
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");
        PrintWriter printWriter = resp.getWriter();
        String city = req.getParameter("city");
        String daysInput = req.getParameter("days");
        Integer days;

        if (city == null) {
            printWriter.write("Пожалуйста, введите город параметром \"city\". " +
                    "</b> Параметр \"days\" для количества дней (от 1 до 5)" +
                    "Например, /weather?city=moscow&days=3");
            return;
        }
        log.info("working with weather for city input: {} for days input: {}", city, daysInput);

        daysInput = daysInput == null ? "1" : daysInput;
        try {
            days = Integer.parseInt(daysInput);
            if (days > 5 || days < 1) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            printWriter.write("Введите количество дней числом от 1 до 5");
            printWriter.close();
            return;
        }

        printWriter.println("<b>Погода в городе: " + city + "<br />" + "<br /> + </b>");

        List<String> weatherReply = weatherService.getWeather(city, days);
        for (String s : weatherReply) {
            printWriter.println(s + "<br />");
        }
        weatherReply = weatherReply.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
        weatherService.saveWeather(city, days, weatherReply);
        printWriter.close();
    }
}