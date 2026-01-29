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

        Util.post(uri, payload);
    }

    public Incident(JsonObject jIncident) {
        // {"checked":false,"closedDate":" ","description":"IBM software detected but no
        // annotations",
        // "id":1764065312,"openingDate":"251125 110835","ownerEmail":"lucile@bnp
        // .com","project":"app2",
        // "status":"Open","truncDescription":"IBM software detected but no
        // annotations"}
        if (jIncident == null) {
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
        this.uri = uri;
        String url = uri + "/" + tid;
        JsonObject jResp = Util.get(url);
        this(jResp);
    }

    public int syncStatus(String status) throws KeyManagementException, NoSuchAlgorithmException {
        setStatus(status);
        setOpened_date(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm:ss")
                .withZone(ZoneId.of("Europe/Paris"))
                .format(Instant.now()));
        setClosed_date(" ");
        setPayload();
        return Util.put(getUri(), getPayload());
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
