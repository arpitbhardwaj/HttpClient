package com.ab.rest;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author Arpit Bhardwaj
 */
public class TotalGoals {
    public static void main(String[] args) throws IOException {
        System.out.println(getTotalGoals("Barcelona", 2011));
    }

    public static int getTotalGoals(String team, int year) throws IOException {
        final String endpoint = "https://jsonmock.hackerrank.com/api/football_matches";
        int totalGoalsAtHome = getPageTotalGoals(
                String.format(endpoint+ "?team1=%s&year=%d",
                        URLEncoder.encode(team,"UTF-8"),
                        year),
                0,
                "team1",
                1
        );
        int totalGoalsAtVisiting = getPageTotalGoals(
                String.format(endpoint+ "?team2=%s&year=%d",
                        URLEncoder.encode(team,"UTF-8"),
                        year),
                0,
                "team2",
                1
        );
        return totalGoalsAtHome+totalGoalsAtVisiting;
    }

    private static int getPageTotalGoals(String request,int totalGoals, String team, int page) throws IOException{
        URL url = new URL(request + "&page=" + page);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setConnectTimeout(5000);
        httpURLConnection.setReadTimeout(5000);
        httpURLConnection.addRequestProperty("Content-Type", "application/json");

        int status = httpURLConnection.getResponseCode();
        InputStream in = (status < 200 || status > 299) ?
                httpURLConnection.getErrorStream() : httpURLConnection.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String responseLine = null;
        StringBuffer responseContent = new StringBuffer();

        while ((responseLine = reader.readLine()) != null){
            responseContent.append(responseLine);
        }

        reader.close();
        httpURLConnection.disconnect();

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        String script = "var obj = JSON.parse('"+responseContent.toString()+"');";
        script += "var total_pages = obj.total_pages;";
        script += "var total_goals = obj.data.reduce(function(accumulator,current) { return accumulator " +
                "+ parseInt(current."+team+"goals);},0);";

        try {
            engine.eval(script);
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        if (engine.get("total_pages") == null){
            throw new RuntimeException("Cannot retrieve data from server");
        }

        int totalPages = (int) engine.get("total_pages");
        totalGoals += (int) Double.parseDouble(engine.get("total_goals").toString());
        return (page < totalPages) ? getPageTotalGoals(request,totalGoals,team, page + 1) : totalGoals;
    }
}
