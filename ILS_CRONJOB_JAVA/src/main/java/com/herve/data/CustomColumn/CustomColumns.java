package com.herve.data.CustomColumn;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CustomColumns {
    private List<CustomColumn> customColumns;

    public CustomColumns(JsonArray jaCustomColumns) {
        List<CustomColumn> customColumns = new ArrayList<>();
        for (JsonElement jcce : jaCustomColumns) {
            JsonObject jcc = jcce.getAsJsonObject();
            if (jcc != null) {
                CustomColumn customColumn = new CustomColumn(jcc);
                customColumns.add(customColumn);
            }
        }
        this.customColumns = customColumns;

    }

    public CustomColumn getCustomColumn(String columnName) {
        for (CustomColumn customColumn : customColumns) {
            if (customColumn.getName().equalsIgnoreCase(columnName)) {
                return customColumn;
            }
        }
        return null;
    }

    public Object getCustomColumnValue(String columnName) {
        CustomColumn customColumn = getCustomColumn(columnName);
        if (customColumn != null) {
            return customColumn.getValue();
        }
        return null;
    }

    
}
