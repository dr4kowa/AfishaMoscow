package com.mirea.kt.ribo.afishamoscow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HTTPRunnableGet implements Runnable {
    private String address;
    private String responseBody;

    public HTTPRunnableGet(String address) {
        this.address = address;
    }

    public String getResponseBody() {
        return responseBody;
    }

    @Override
    public void run() {
        if (this.address != null && !this.address.isEmpty()) {
            try {
                URL url = new URL(this.address);
                URLConnection connection = url.openConnection();
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                InputStreamReader isr = new InputStreamReader(httpConnection.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                String currentLine;
                StringBuilder sbResponse = new StringBuilder();
                while ((currentLine = br.readLine()) != null) {
                    sbResponse.append(currentLine);
                }
                responseBody = sbResponse.toString();
            } catch (IOException ex) {
            }
        }
    }
}

