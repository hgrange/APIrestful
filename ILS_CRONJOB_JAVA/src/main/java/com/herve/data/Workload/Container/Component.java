package com.herve.data.Workload.Container;

import com.herve.Util;
import com.google.gson.JsonObject;

public class Component {
    private String id;
    private String name;
    private String version;
    private String installationPath;

    public Component(JsonObject jComponent) {

        setId((String)Util.map(jComponent, "id", "String"));
        setName((String)Util.map(jComponent, "name", "String"));
        setVersion((String)Util.map(jComponent, "version", "String"));
        setInstallationPath((String)Util.map(jComponent, "installationPath", "String"));
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getInstallationPath() {
        return installationPath;
    }

    public void setInstallationPath(String installationPath) {
        this.installationPath = installationPath;
    }



}
