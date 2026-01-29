package com.herve.data.CustomColumn;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CustomColumnIDs {

    private List<CustomColumnID> customColumnIDs;
   

    public CustomColumnIDs(JsonArray jaCColumns) {
        List<CustomColumnID> customColumnIDs = new ArrayList<>();
        for (JsonElement jccide : jaCColumns) {
            JsonObject jcc = jccide.getAsJsonObject();
            if (jcc != null) {
                CustomColumnID customColumnID = new CustomColumnID(jcc);
                customColumnIDs.add(customColumnID);
            }
        }
        this.customColumnIDs = customColumnIDs;
    }

    public int getColumnID(String columnName) {
        for (CustomColumnID customColumnID : customColumnIDs) {
            if (customColumnID.getName().equalsIgnoreCase(columnName)) {
                return customColumnID.getId();
            }
        }
        return 0;
    }

}
