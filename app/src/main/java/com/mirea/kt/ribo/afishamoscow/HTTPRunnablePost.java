package com.mirea.kt.ribo.afishamoscow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

public class HTTPRunnablePost implements Runnable {
    private String address, responseBody;
    private HashMap<String, String> requestBody;

    public HTTPRunnablePost(String address, HashMap<String, String> requestBody) {
        this.address = address;
        this.requestBody = requestBody;
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
                HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStreamWriter osw = new OutputStreamWriter(httpURLConnection.getOutputStream());
                osw.write(generateStringBody());
                osw.flush();
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
                    InputStreamReader isr = new InputStreamReader(httpURLConnection.getInputStream());
                    BufferedReader br = new BufferedReader(isr);
                    String currentLine;
                    StringBuilder sbResponse = new StringBuilder();
                    while ((currentLine = br.readLine()) != null) {
                        sbResponse.append(currentLine);
                    }
                    responseBody = sbResponse.toString();
                } else {
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String generateStringBody() {
        StringBuilder sbParams = new StringBuilder();
        if (this.requestBody != null && !requestBody.isEmpty()) {
            int i = 0;
            for (String key : this.requestBody.keySet()) {
                try {
                    if (i != 0) {
                        sbParams.append("&");
                    }
                    sbParams.append(key).append("=")
                            .append(URLEncoder.encode(this.requestBody.get(key), "UTF-8"));
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
                i++;
            }
        }
        return sbParams.toString();
    }
}
