package org.example.ticket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        String file;
        try {
            InputStream input = Main.class.getClassLoader().getResourceAsStream("application.properties");
            Properties prop = new Properties();
            prop.load(input);
            file = prop.getProperty("file");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(file)));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        JSONObject obj = new JSONObject(content);
        JSONArray tickets = obj.getJSONArray("tickets");

        ArrayList<Integer> flightTimes = new ArrayList<>();
        for (int i = 0; i < tickets.length(); i++) {
            JSONObject ticket = tickets.getJSONObject(i);
            if (ticket.getString("origin").equals("VVO") && ticket.getString("destination").equals("TLV")) {
                String[] depTimeParts = ticket.getString("departure_time").split(":");
                String[] arrTimeParts = ticket.getString("arrival_time").split(":");
                int depTime = Integer.parseInt(depTimeParts[0]) * 60 + Integer.parseInt(depTimeParts[1]);
                int arrTime = Integer.parseInt(arrTimeParts[0]) * 60 + Integer.parseInt(arrTimeParts[1]);
                int flightTime = arrTime - depTime;
                flightTimes.add(flightTime);
            }
        }

        double avgFlightTime = flightTimes.stream().mapToDouble(Integer::doubleValue).average().orElse(0);
        logger.info("Среднее время полета между Владивостоком и Тель-Авивом: {} минут", avgFlightTime);

        Collections.sort(flightTimes);
        double percentile90 = flightTimes.get((int) Math.ceil(flightTimes.size() * 0.9) - 1);
        logger.info("90-й процентиль времени полета между Владивостоком и Тель-Авивом: {} минут", percentile90);
    }
}