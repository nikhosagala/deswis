package com.deswis.utils;

import play.Play;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class Config {

    private static Config instance = null;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private final SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss'T'dd-MM-yyyy");
    private final SimpleDateFormat sdfWithDay = new SimpleDateFormat("EEEE, d MMMM yyyy, HH:mm:ss");

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public String getStringDate(Date date) {
        if (date != null) {
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+7"));
            return sdf.format(date);
        } else {
            return null;
        }
    }

    private String getStringDate2(Date date) {
        if (date != null) {
            sdf2.setTimeZone(TimeZone.getTimeZone("GMT+7"));
            return sdf2.format(date);
        } else {
            return null;
        }
    }

    public String getStringWithDay(Date date) {
        if (date != null) {
            sdfWithDay.setTimeZone(TimeZone.getTimeZone("GMT+7"));
            return sdfWithDay.format(date);
        } else {
            return null;
        }
    }

    public String getSuccessActivity(String activity) {
        return "Success doing " + activity + " at: " + getStringDate2(new Date());
    }

    public String getErrorActivity(String activity, String error) {
        return "Error doing " + activity + " at: " + getStringDate2(new Date()) + " " + error;
    }

    public String getBadRequestMessage() {
        return "Internal server error.";
    }

    public String getAPIKey() {
        Random random = new Random();
        switch (random.nextInt(3)) {
            case 0:
                return Play.application().configuration().getString("maps_api_1");
            case 1:
                return Play.application().configuration().getString("maps_api_2");
            case 2:
                return Play.application().configuration().getString("maps_api_3");
            default:
                return Play.application().configuration().getString("maps_api_1");
        }
    }

    public String getGithubApiKey() {
        return Play.application().configuration().getString("github_key");
    }

    public String getPath() {
        String path;
        try {
            path = Play.application().path() + File.separator;
        } catch (RuntimeException e) {
            path = "";
        }
        return path;
    }
}
