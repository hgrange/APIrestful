package com.herve.data.Workload.Container;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.herve.Util;

public class Container {
    private String name;
    private String image;
    private Boolean charged;
    private List<Component> components;

    public Container(JsonObject jContainer) {
        setName((String) Util.map(jContainer, "name", "String"));
        setImage((String) Util.map(jContainer, "image", "String"));
        setCharged((Boolean) Util.map(jContainer, "charged", "Boolean"));
        setComponents((JsonArray) Util.map(jContainer, "components", "JsonArray"));
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getCharged() {
        return charged;
    }

    public void setCharged(Boolean charged) {
        this.charged = charged;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(JsonArray jaComponents) {
        if (jaComponents == null) {
            this.components = null;
        } else {
            List<Component> components = new ArrayList<>();
            for (JsonElement jco : jaComponents) {
                JsonObject jComponent = jco.getAsJsonObject();
                if (jComponent != null) {
                    Component component = new Component(jComponent);
                    components.add(component);
                }
            }
            this.components = components;
        }
    }

}
