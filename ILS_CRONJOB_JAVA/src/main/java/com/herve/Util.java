package com.herve;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class Util {

    // Debug flag - set to true to enable trace logging
    private static final boolean DEBUG = "true".equalsIgnoreCase(System.getenv("DEBUG"));

    // Private constructor to prevent instantiation
    private Util() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static HttpClient getHttpClient() throws NoSuchAlgorithmException, KeyManagementException {
        if (DEBUG) System.out.println("[DEBUG] getHttpClient() - Creating HTTP client");
        HttpClient client = null;
        boolean insecure = ("true".equalsIgnoreCase(System.getenv("INSECURE"))) ? true : false;
        if (DEBUG) System.out.println("[DEBUG] getHttpClient() - Insecure mode: " + insecure);
        SSLContext sslContext = null;
        if (insecure) {
            // Trust all certificates
            System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");

            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            SSLParameters sslParams = new SSLParameters();
            sslParams.setEndpointIdentificationAlgorithm(null); // This disables hostname verification

            client = HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .sslParameters(sslParams)
                    .build();
            if (DEBUG) System.out.println("[DEBUG] getHttpClient() - Created insecure HTTP client");

        } else {
            System.setProperty("javax.net.ssl.trustStore", "C:\\data\\workspaces\\ILS\\truststore.p12");
            System.setProperty("javax.net.ssl.trustStorePassword", "password");
            System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");
            client = HttpClient.newHttpClient();
            if (DEBUG) System.out.println("[DEBUG] getHttpClient() - Created secure HTTP client with truststore");
        }

        return client;
    }

    public static JsonArray download(String url, String filename)
            throws KeyManagementException, NoSuchAlgorithmException {

        if (DEBUG) System.out.println("[DEBUG] download() - URL: " + url + ", Filename: " + filename);
        String records = null;
        JsonArray jaRecord = null;
        HttpClient client = getHttpClient();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> resp;

        try {
            resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (DEBUG) System.out.println("[DEBUG] download() - Response status: " + resp.statusCode());

            if (resp.statusCode() == 200) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
                StringReader sr = new StringReader(resp.body());
                int intValueOfChar;
                records = "";

                while ((intValueOfChar = sr.read()) != -1) {
                    records += (char) intValueOfChar;
                }
                jaRecord = JsonParser.parseString(records).getAsJsonArray();
                if (DEBUG) System.out.println("[DEBUG] download() - Parsed " + jaRecord.size() + " records");
                for (JsonElement jer : jaRecord) {
                    String record = jer.getAsJsonObject().toString();
                    writer.write(record + "\n");
                }
                sr.close();
                writer.close();
                client.close();
            }

        } catch (

        IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jaRecord;
    }

    public static String getToken(String fileName) {
        if (DEBUG) System.out.println("[DEBUG] getToken() - Reading token from: " + fileName);
        BufferedReader reader;
        String token = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            token = reader.readLine();
            if (DEBUG) System.out.println("[DEBUG] getToken() - Token retrieved successfully");

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return token;
    }

    public static JsonArray readFileData(String fileName) {
        if (DEBUG) System.out.println("[DEBUG] readFileData() - Reading from: " + fileName);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            JsonArray jArray = new JsonArray();
            while (line != null) {
                JsonObject jObj = JsonParser.parseString(line).getAsJsonObject();
                jArray.add(jObj);
                line = reader.readLine();
            }
            reader.close();
            if (DEBUG) System.out.println("[DEBUG] readFileData() - Read " + jArray.size() + " records");
            return jArray;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to persist custom column data
    public static int persist_data_customColumns(String apiHost, String token, int recordID, int columnID, String value)
            throws KeyManagementException, NoSuchAlgorithmException {
        if (DEBUG) System.out.println("[DEBUG] persist_data_customColumns() - RecordID: " + recordID + ", ColumnID: " + columnID + ", Value: " + value);
        int status = -1;
        if (value != null) {
            String url = "https://" + apiHost + "/workloads/" + recordID + "/custom_columns/" + columnID + "?token="
                    + token;
            status = Util.put(url, "{\"value\":\"" + value + "\"}");
            if (DEBUG) System.out.println("[DEBUG] persist_data_customColumns() - Status: " + status);
        }
        return status;
    }

    public static int clear_data_customColumns(String apiHost, String token, int recordID, int columnID)
            throws KeyManagementException, NoSuchAlgorithmException {
        if (DEBUG) System.out.println("[DEBUG] clear_data_customColumns() - RecordID: " + recordID + ", ColumnID: " + columnID);
        int status = -1;
        String url = "https://" + apiHost + "/workloads/" + recordID + "/custom_columns/" + columnID + "?token="
                + token;
        status = Util.delete(url, token);
        if (DEBUG) System.out.println("[DEBUG] clear_data_customColumns() - Status: " + status);
        return status;
    }

    public static int post(String url, String payload) throws KeyManagementException, NoSuchAlgorithmException {
        if (DEBUG) System.out.println("[DEBUG] post() - URL: " + url);
        if (DEBUG) System.out.println("[DEBUG] post() - Payload: " + payload);
        // String url = "https://" + itsmHost + "/v2/incident?token=" + token;
        HttpClient client = Util.getHttpClient();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        HttpResponse<String> resp;

        try {
            resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (DEBUG) System.out.println("[DEBUG] post() - Response status: " + resp.statusCode());
            if (!String.valueOf(resp.statusCode()).startsWith("20")) {
                throw new RuntimeException("Failed to create incident, HTTP status code: " + resp.statusCode());
            }
            return resp.statusCode();
        } catch (

        IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }

        return 0;
    }

    public static int put(String url, String payload) throws KeyManagementException, NoSuchAlgorithmException {
        if (DEBUG) System.out.println("[DEBUG] put() - URL: " + url);
        if (DEBUG) System.out.println("[DEBUG] put() - Payload: " + payload);
        // String url = "https://" + itsmHost + "/v2/incident?token=" + token;
        HttpClient client = Util.getHttpClient();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        HttpResponse<String> resp;

        try {
            resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (DEBUG) System.out.println("[DEBUG] put() - Response status: " + resp.statusCode());
            if (!String.valueOf(resp.statusCode()).startsWith("20")) {
                throw new RuntimeException("Failed to create incident, HTTP status code: " + resp.statusCode());
            }
            return resp.statusCode();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }

        return 0;
    }

    public static int delete(String url, String token)
            throws KeyManagementException, NoSuchAlgorithmException {
        if (DEBUG) System.out.println("[DEBUG] delete() - URL: " + url);
        int status = -1;

        HttpClient client = getHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();
        try {
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (DEBUG) System.out.println("[DEBUG] delete() - Response status: " + resp.statusCode());
            if (!String.valueOf(resp.statusCode()).startsWith("20"))
                throw new RuntimeException("Failed to create incident, HTTP status code: " + resp.statusCode());
            return resp.statusCode();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return status;
    }

    public static JsonObject get(String url)
            throws KeyManagementException, NoSuchAlgorithmException, IOException, InterruptedException {
        if (DEBUG) System.out.println("[DEBUG] get() - URL: " + url);
        HttpClient client = Util.getHttpClient();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (DEBUG) System.out.println("[DEBUG] get() - Response status: " + resp.statusCode());
        String body = resp.body();
        JsonObject jResp = null;
        if ( ! body.isEmpty())
            jResp = JsonParser.parseString(body).getAsJsonObject();
        if (DEBUG) System.out.println("[DEBUG] get() - Response body parsed successfully");
        return jResp;

    }

    public static String get(String url, String para) throws KeyManagementException, NoSuchAlgorithmException {
        if (DEBUG) System.out.println("[DEBUG] get(url, para) - URL: " + url + ", Parameter: " + para);

        HttpClient client = Util.getHttpClient();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> resp;

        try {
            resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (DEBUG) System.out.println("[DEBUG] get(url, para) - Response status: " + resp.statusCode());
            StringReader sr = new StringReader(resp.body());
            int intValueOfChar;
            String response = "";

            while ((intValueOfChar = sr.read()) != -1) {
                response += (char) intValueOfChar;
            }
            sr.close();
            JsonObject jResp = JsonParser.parseString(response).getAsJsonObject();
            String paraValue = (String) Util.map(jResp, para, "String");
            if (DEBUG) System.out.println("[DEBUG] get(url, para) - Parameter value: " + paraValue);
            return paraValue;
        } catch (

        IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }

        return null;
    }

    public static Object map(JsonObject jobj, String name, String type) {
        if (DEBUG) System.out.println("[DEBUG] map() - Name: " + name + ", Type: " + type);
        JsonElement jsonElement = jobj.get(name);
        if (jsonElement instanceof JsonNull || jsonElement == null) {
            return null;
        } else {
            if (type == "String") {
                return jsonElement.getAsString();
            } else if (type == "int") {
                return jsonElement.getAsInt();
            } else if (type == "Boolean") {
                return jsonElement.getAsBoolean();
            } else if (type == "JsonArray") {
                return jsonElement.getAsJsonArray();
            }
        }
        return null;
    }
}
