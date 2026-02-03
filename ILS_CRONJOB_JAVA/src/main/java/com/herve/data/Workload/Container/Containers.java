package com.herve.data.Workload.Container;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Containers {
    // Debug flag - set to true to enable trace logging
    private static final boolean DEBUG = "true".equalsIgnoreCase(System.getenv("DEBUG"));

    private List<Container> containers;
    private String charged;


    public Containers(JsonArray jaContainers) {
        if (DEBUG) System.out.println("[DEBUG] Containers.Containers() - Creating Containers from JsonArray");
        List<Container> containers = new ArrayList<>();

        charged = "false";
        for (JsonElement jContainer : jaContainers) {
            JsonObject jContainerObj = jContainer.getAsJsonObject();
            if (jContainerObj != null) {
                Container container = new Container(jContainerObj);
                containers.add(container);

                if (container.getCharged() == null) {
                    if (charged.equals("false")) charged = "null";
                } else if (container.getCharged() == true) {
                    charged = "true";
                }

            }
        }
        this.setCharged(charged);
        this.containers = containers;
        if (DEBUG) System.out.println("[DEBUG] Containers.Containers() - Loaded " + containers.size() + " containers, Charged: " + charged);
    }

    public String getCharged() {
        return charged;
    }

    public void setCharged(String charged) {
        this.charged = charged;
    }

    public List<Container> getContainers() {
        return containers;
    }

    public void setContainers(List<Container> containers) {
        this.containers = containers;
    }

}
