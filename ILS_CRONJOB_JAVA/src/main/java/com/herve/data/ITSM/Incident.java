package com.herve.data.ITSM;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonObject;
import com.herve.Util;

public class Incident {
    // Debug flag - set to true to enable trace logging
    private static final boolean DEBUG = "true".equalsIgnoreCase(System.getenv("DEBUG"));


    private String tid;
    private String title;
    private String comment;
    private String status;
    private String ownerEmail;
    private String project;
    private String opened_date;
    private String closed_date;
    private String uri;
    private String token;
    private String payload;

    public Incident(String uri, String token, String ownerEmail, String project, String comment)
            throws KeyManagementException, NoSuchAlgorithmException {
        if (DEBUG) System.out.println("[DEBUG] Incident.Incident() - Creating new incident");
        if (ownerEmail == null)
            ownerEmail = "Unknown";
        if (project == null)
            project = "Unknown";

        setTid(String.valueOf(Instant.now().getEpochSecond()));
        setTitle(title);
        setComment(comment);
        setStatus("Open");
        setOwnerEmail(ownerEmail);
        setProject(project);
        setOpened_date(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm:ss")
                .withZone(ZoneId.of("Europe/Paris"))
                .format(Instant.now()));
        setClosed_date(" ");
        setUri(uri);
        setToken(token);

        // Prepare the title and default ownerEmail before calling the other constructor
        String title = (comment == null)
                ? "No or wrong assertion(s)"
                : comment;
        setTitle(title);
        setPayload();
        if (DEBUG) System.out.println("[DEBUG] Incident.Incident() - TID: " + tid + ", Project: " + project + ", Owner: " + ownerEmail);

        Util.post(uri, payload);
    }

    public Incident(JsonObject jIncident) {
        if (DEBUG) System.out.println("[DEBUG] Incident.Incident() - Creating incident from JsonObject");
        // {"checked":false,"closedDate":" ","description":"IBM software detected but no
        // annotations",
        // "id":1764065312,"openingDate":"251125 110835","ownerEmail":"lucile@bnp
        // .com","project":"app2",
        // "status":"Open","truncDescription":"IBM software detected but no
        // annotations"}
        if (jIncident == null) {
            if (DEBUG) System.out.println("[DEBUG] Incident.Incident() - JsonObject is null");
            setTid(null);
            setOpened_date(null);
            setClosed_date(null);
            setStatus(null);
            setOwnerEmail(null);
            setProject(null);
            setTitle(null);
            setComment(null);
        } else {
            JsonObject jObj = jIncident.getAsJsonObject();

            setTid((String) Util.map(jObj, "id", "String"));
            if (DEBUG) System.out.println("[DEBUG] Incident.Incident() - TID: " + getTid());
            setOpened_date((String) Util.map(jObj, "openingDate", "String"));
            setClosed_date((String) Util.map(jObj, "closedDate", "String"));
            setStatus((String) Util.map(jObj, "status", "String"));
            setOwnerEmail((String) Util.map(jObj, "ownerEmail", "String"));
            setProject((String) Util.map(jObj, "project", "String"));
            setTitle((String) Util.map(jObj, "truncDescription", "String"));
            setComment((String) Util.map(jObj, "description", "String"));
        }
    }

    public Incident(String uri, String tid)
            throws KeyManagementException, NoSuchAlgorithmException, IOException, InterruptedException {
        if (DEBUG) System.out.println("[DEBUG] Incident.Incident() - Fetching incident from URI: " + uri + ", TID: " + tid);
        this.uri = uri;
        String url = uri + "/" + tid;
        JsonObject jResp = Util.get(url);
        this(jResp);
    }

    public int syncStatus(String status) throws KeyManagementException, NoSuchAlgorithmException {
        if (DEBUG) System.out.println("[DEBUG] Incident.syncStatus() - Syncing status to: " + status + " for TID: " + getTid());
        setStatus(status);
        setOpened_date(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm:ss")
                .withZone(ZoneId.of("Europe/Paris"))
                .format(Instant.now()));
        setClosed_date(" ");
        setPayload();
        int result = Util.put(getUri(), getPayload());
        if (DEBUG) System.out.println("[DEBUG] Incident.syncStatus() - Sync result: " + result);
        return result;
    }

    public void setPayload() {
        this.payload = "{\"checked\":\"false\",\"id\":" + getTid() +
                ",\"title\":\"" + title +
                "\",\"description\":\"" + getComment() + "\",\"project\":\"" + getProject() +
                "\",\"ownerEmail\":\"" + getOwnerEmail() + "\",\"openingDate\":\"" + getOpened_date() +
                "\",\"closedDate\":\" \",\"status\":\"" + getStatus() + "\"}";
    }

    private void setStatus(String string) {
        this.status = string;
    }

    public String getStatus() {
        return this.status;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getOpened_date() {
        return opened_date;
    }

    public void setOpened_date(String opened_date) {
        this.opened_date = opened_date;
    }

    public String getClosed_date() {
        return closed_date;
    }

    public void setClosed_date(String closed_date) {
        this.closed_date = closed_date;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPayload() {
        return payload;
    }

}
