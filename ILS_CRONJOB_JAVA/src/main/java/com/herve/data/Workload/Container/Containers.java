package com.herve.data.Workload.Container;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Containers {

    private List<Container> containers;
    private String charged;


    public Containers(JsonArray jaContainers) {
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
