package com.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class CelcoinAPI {

    public static void main(String[] args) {
        try {
            String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGllbnRfaWQiOiI0MWI0NGFiOWE1NjQ0MC50ZXN0ZS5jZWxjb2luYXBpLnY1IiwiaHR0cDovL3NjaGVtYXMueG1sc29hcC5vcmcvd3MvMjAwNS8wNS9pZGVudGl0eS9jbGFpbXMvbmFtZSI6InRlc3RlIiwiaHR0cDovL3NjaGVtYXMubWljcm9zb2Z0LmNvbS93cy8yMDA4LzA2L2lkZW50aXR5L2NsYWltcy91c2VyZGF0YSI6IjdlNmMwZGZkZmM1MzRlNDM5M2M0IiwiZXhwIjoxNzAwNjc0MzIxLCJpc3MiOiJDZWxjb2luQVBJIiwiYXVkIjoiQ2VsY29pbkFQSSJ9.6q_L_VOuDf_Ksw33oCeYMK2S3H_SlmJGI1dCYwccJDk";
            System.out.println("Access Token: " + accessToken);

            int qrCodeData = createQRCode(accessToken);
            System.out.println("QR Code Data: " + qrCodeData);

            getImage(accessToken, qrCodeData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int createQRCode(String accessToken) throws Exception {
        URL url = new URL("https://sandbox.openfinance.celcoin.dev/pix/v1/brcode/static");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("accept", "application/json");
        connection.setRequestProperty("content-type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setDoOutput(true);

        JSONObject merchant = new JSONObject();
        merchant.put("postalCode", "01201005");
        merchant.put("city", "Barueri");
        merchant.put("merchantCategoryCode", "0");
        merchant.put("name", "Celcoin");

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("key", "testepix@celcoin.com.br");
        jsonParams.put("amount", 10.55);
        jsonParams.put("transactionIdentification", "testqrcodestaticcelcoin");
        jsonParams.put("merchant", merchant);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonParams.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getInt("transactionId");
        }
    }

    private static void getImage(String accessToken, int transactionId) throws Exception {
        URL url = new URL("https://sandbox.openfinance.celcoin.dev/pix/v1/brcode/static/"
                + transactionId + "/base64");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            JSONObject jsonResponse = new JSONObject(response.toString());
            System.out.println("Base64 Image: " + jsonResponse.getString("base64image"));
        }
    }
}