package com.herve.data.CMDB;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class CMDBs {
    private List<CMDB> cmdbs;

    
    public CMDBs(JsonArray jaCMDBs) {
        List<CMDB> cmdbs = new ArrayList<>();
        for (JsonElement jcmdbe : jaCMDBs) {
            JsonObject jcmdb = jcmdbe.getAsJsonObject();
            if (jcmdb != null) {
                CMDB cmdb = new CMDB(jcmdb);
                cmdbs.add(cmdb);
            }
        }
        this.cmdbs = cmdbs;
    }
    public CMDB getCMDB(String clusterName, String namespace) {
        for (CMDB cmdb : cmdbs) {
            if (cmdb.getNamespace().equals(namespace) && cmdb.getCluster().equals(clusterName)) {
                return cmdb;
            }
        }
        return new CMDB();
    }

}
