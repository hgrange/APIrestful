package com.herve.data.CustomColumn;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class CustomColumn {
    // Debug flag - set to true to enable trace logging
    private static final boolean DEBUG = "true".equalsIgnoreCase(System.getenv("DEBUG"));

    private int id;
    private String name;
    private String value;

    public CustomColumn(JsonObject jCustomColumn) {
        if (DEBUG) System.out.println("[DEBUG] CustomColumn.CustomColumn() - Creating CustomColumn from JsonObject");
        setId((int) this.map(jCustomColumn, "id", "int"));
        setName((String) this.map(jCustomColumn, "name", "String"));
        setValue((String) this.map(jCustomColumn, "value", "String"));
        if (DEBUG) System.out.println("[DEBUG] CustomColumn.CustomColumn() - ID: " + id + ", Name: " + name + ", Value: " + value);
    }

    Object map(JsonObject jobj, String name, String type) {
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

    public int persist(String apiHost, String token, int recordID, int columnID, String value) {
        if (DEBUG) System.out.println("[DEBUG] CustomColumn.persist() - RecordID: " + recordID + ", ColumnID: " + columnID + ", Value: " + value);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://" + apiHost + "/workload/" + recordID + "/custom_columns/" + columnID
                        + "?token=" + token))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"value\":\"" + value + "\"}"))
                .build();
        HttpResponse<String> resp;

        try {
            resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            client.close();
            if (DEBUG) System.out.println("[DEBUG] CustomColumn.persist() - Response status: " + resp.statusCode());
            if (resp.statusCode() == 200) {
                setValue(value);
            } else {
                System.out.println("[ERROR] CustomColumn.persist() - Failed with status " + resp.statusCode() + ": " + resp.body());
            }
            return resp.statusCode();

        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.value = null;
        return 0;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
