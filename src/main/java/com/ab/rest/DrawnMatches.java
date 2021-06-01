package com.ab.rest;

import javax.script.*;
import java.io.*;
import java.net.*;

/**
 * @author Arpit Bhardwaj
 */
public class DrawnMatches {
    public static void main(String[] args) throws IOException {
        System.out.println(getNumDraws(2011));
    }

    public static int getNumDraws(int year) throws IOException {
        final String endpoint = "https://jsonmock.hackerrank.com/api/football_matches?year=" + year;
        final int maxScore = 10;
        int totalNumDraws = 0;

        for (int score = 0; score <= maxScore; score++) {
            totalNumDraws += getTotalNumDraws(String.format(endpoint + "&team1goals=%d&team2goals=%d",
                    score,
                    score));
        }
        return totalNumDraws;
    }

    private static int getTotalNumDraws(String request) throws IOException {
        URL url = new URL(request);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setConnectTimeout(120000);
        httpURLConnection.setReadTimeout(120000);
        httpURLConnection.addRequestProperty("Content-Type", "application/json");

        int status = httpURLConnection.getResponseCode();
        InputStream in = (status < 200 || status > 299) ?
                httpURLConnection.getErrorStream() : httpURLConnection.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String responseLine;
        StringBuffer responseContent = new StringBuffer();

        while ((responseLine = reader.readLine()) != null){
            responseContent.append(responseLine);
        }

        reader.close();
        httpURLConnection.disconnect();

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        String script = "var obj = JSON.parse('"+responseContent+"');";
        script += "var total = obj.total;";

        try {
            engine.eval(script);
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        if (engine.get("total") == null){
            throw new RuntimeException("Cannot retrieve data from server");
        }
        return (int) engine.get("total");
    }
}
