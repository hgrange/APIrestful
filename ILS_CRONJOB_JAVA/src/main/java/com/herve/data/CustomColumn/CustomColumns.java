package com.herve.data.CustomColumn;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CustomColumns {
    // Debug flag - set to true to enable trace logging
    private static final boolean DEBUG = "true".equalsIgnoreCase(System.getenv("DEBUG"));
    
    private List<CustomColumn> customColumns;

    public CustomColumns(JsonArray jaCustomColumns) {
        if (DEBUG) System.out.println("[DEBUG] CustomColumns.CustomColumns() - Creating CustomColumns from JsonArray");
        List<CustomColumn> customColumns = new ArrayList<>();
        for (JsonElement jcce : jaCustomColumns) {
            JsonObject jcc = jcce.getAsJsonObject();
            if (jcc != null) {
                CustomColumn customColumn = new CustomColumn(jcc);
                customColumns.add(customColumn);
            }
        }
        this.customColumns = customColumns;
        if (DEBUG) System.out.println("[DEBUG] CustomColumns.CustomColumns() - Loaded " + customColumns.size() + " custom columns");
    }

    public CustomColumn getCustomColumn(String columnName) {
        if (DEBUG) System.out.println("[DEBUG] CustomColumns.getCustomColumn() - Looking for column: " + columnName);
        for (CustomColumn customColumn : customColumns) {
            if (customColumn.getName().equalsIgnoreCase(columnName)) {
                if (DEBUG) System.out.println("[DEBUG] CustomColumns.getCustomColumn() - Found column: " + columnName);
                return customColumn;
            }
        }
        if (DEBUG) System.out.println("[DEBUG] CustomColumns.getCustomColumn() - Column not found: " + columnName);
        return null;
    }

    public Object getCustomColumnValue(String columnName) {
        if (DEBUG) System.out.println("[DEBUG] CustomColumns.getCustomColumnValue() - Getting value for column: " + columnName);
        CustomColumn customColumn = getCustomColumn(columnName);
        if (customColumn != null) {
            Object value = customColumn.getValue();
            if (DEBUG) System.out.println("[DEBUG] CustomColumns.getCustomColumnValue() - Value: " + value);
            return value;
        }
        if (DEBUG) System.out.println("[DEBUG] CustomColumns.getCustomColumnValue() - No value found for column: " + columnName);
        return null;
    }

    
}
