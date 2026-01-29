package com.herve.data.CMDB;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class CMDB {
    private String cluster;
    private String namespace;
    private String ownerEmail;
    private String project;
    private String sid;

    

    public CMDB(JsonObject jCMDB) {
        setCluster((String) this.map(jCMDB, "cluster", "String"));
        setNamespace((String) this.map(jCMDB, "namespace", "String"));
        setOwnerEmail((String) this.map(jCMDB, "ownerEmail", "String"));
        setProject((String) this.map(jCMDB, "project", "String"));
        setSid((String) this.map(jCMDB, "sid", "String"));
    }

    public CMDB() {
        setCluster(null);
        setNamespace(null);
        setOwnerEmail(null);
        setProject(null);
        setSid(null);
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

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}