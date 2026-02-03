package com.herve.data.CMDB;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class CMDBs {
    // Debug flag - set to true to enable trace logging
    private static final boolean DEBUG = "true".equalsIgnoreCase(System.getenv("DEBUG"));
    
    private List<CMDB> cmdbs;

    
    public CMDBs(JsonArray jaCMDBs) {
        if (DEBUG) System.out.println("[DEBUG] CMDBs.CMDBs() - Loading CMDBs from JsonArray");
        List<CMDB> cmdbs = new ArrayList<>();
        for (JsonElement jcmdbe : jaCMDBs) {
            JsonObject jcmdb = jcmdbe.getAsJsonObject();
            if (jcmdb != null) {
                CMDB cmdb = new CMDB(jcmdb);
                cmdbs.add(cmdb);
            }
        }
        this.cmdbs = cmdbs;
        if (DEBUG) System.out.println("[DEBUG] CMDBs.CMDBs() - Loaded " + cmdbs.size() + " CMDBs");
    }
    public CMDB getCMDB(String clusterName, String namespace) {
        if (DEBUG) System.out.println("[DEBUG] CMDBs.getCMDB() - Looking for CMDB with cluster: " + clusterName + ", namespace: " + namespace);
        for (CMDB cmdb : cmdbs) {
            if (cmdb.getNamespace().equals(namespace) && cmdb.getCluster().equals(clusterName)) {
                if (DEBUG) System.out.println("[DEBUG] CMDBs.getCMDB() - Found matching CMDB");
                return cmdb;
            }
        }
        if (DEBUG) System.out.println("[DEBUG] CMDBs.getCMDB() - No matching CMDB found, returning empty CMDB");
        return new CMDB();
    }

}
