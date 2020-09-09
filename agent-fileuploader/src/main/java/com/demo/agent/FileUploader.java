package com.demo.agent;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUploader {

    private static String getBearerToken(String clientId, String clientSecret, String serverHost) throws IOException {
        String payload = String.format("{\"clientId\":\"%s\",\"secret\":\"%s\"}", clientId, clientSecret);
        URL endPoint = new URL(serverHost + "/auth/token");
        HttpURLConnection con = (HttpURLConnection) endPoint.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = payload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            String bearerTokenJSON = response.toString();
            String[] bearerTokenSplits = bearerTokenJSON.split(",");
            for (String split : bearerTokenSplits) {
                if (split.contains("accessToken")) {
                    String tokenWithQuotes = split.split(":")[1];
                    return tokenWithQuotes.replace("\"", "");
                }
            }
        }
        return null;
    }

    private static final String uploadFile(String accountId, String fileName, String bearerToken, String serverHost) throws IOException {
        URL endPoint = new URL(serverHost + "/data/" + accountId + "/uploadFile");
        String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
        HttpURLConnection con = (HttpURLConnection) endPoint.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        con.setRequestProperty("Authorization", "Bearer " + bearerToken);
        con.setDoOutput(true);


        String charset = "UTF-8";
        String param = "value";
        File textFile = new File(fileName);
        String CRLF = "\r\n"; // Line separator required by multipart/form-data.

        try (
                OutputStream output = con.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
        ) {
            // Send text file.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + textFile.getName() + "\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF); // Text file itself must be saved in this charset!
            writer.append(CRLF).flush();
            Files.copy(textFile.toPath(), output);
            output.flush(); // Important before continuing with writer!
            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.
            // End of multipart/form-data.
            writer.append("--" + boundary + "--").append(CRLF).flush();
        }


        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Usage : java -jar <jarName> <accessKey> <accessSecret> <fileToUpload> [<serverHost>]");
            return;
        }
        String clientId = args[0];
        String clientSecret = args[1];
        String fileToUpload = args[2];
        String accountId = clientId.split("-")[0];

        System.out.println("accountId - " + accountId);
        System.out.println("accessKey - " + clientId);
        System.out.println("accessSecret - " + clientSecret);
        System.out.println("fileToUpload - " + fileToUpload);

        if (!Files.exists(Paths.get(fileToUpload))) {
            throw new FileNotFoundException(fileToUpload);
        }

        String serverHost = args.length == 4 ? args[3] : "http://localhost:8080";

        String bearerToken = getBearerToken(clientId, clientSecret, serverHost);
        System.out.println("bearerToken - " + bearerToken);

        String uploadResponse = uploadFile(accountId, fileToUpload, bearerToken, serverHost);
        System.out.println("uploadResponse - " + uploadResponse);
    }
}
