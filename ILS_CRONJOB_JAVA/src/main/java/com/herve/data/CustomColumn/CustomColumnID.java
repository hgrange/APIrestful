package com.herve.data.CustomColumn;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class CustomColumnID {
    private int id;
    private String name;
    private String type; 


    public CustomColumnID(JsonObject jCustomColumn) {
        setId((int) this.map(jCustomColumn, "id", "int"));
        setName((String) this.map(jCustomColumn, "name", "String"));
        setType((String) this.map(jCustomColumn, "type", "String"));
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
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
   

}
