package com.herve;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.herve.data.Constants;
import com.herve.data.CMDB.CMDB;
import com.herve.data.CMDB.CMDBs;
import com.herve.data.CustomColumn.CustomColumnIDs;
import com.herve.data.CustomColumn.CustomColumns;
import com.herve.data.ITSM.Incident;
import com.herve.data.Workload.Workload;

public class CronWLs {

    public static void main(String[] args) throws NoSuchAlgorithmException, KeyManagementException, InterruptedException {

        if (args.length != 3) {
            System.out.println("Usage: java -jar CronWLs.jar <apiHost> <cmdbHost> <itsmHost>");
            System.exit(1);
        }
        boolean debug = ("true".equalsIgnoreCase(System.getenv("DEBUG"))) ? true : false;
        boolean bouchon = ("true".equalsIgnoreCase(System.getenv("BOUCHON"))) ? true : false;

        String token = Util.getToken("lsrtoken/token");
        if (debug)
            System.out.println("CronWLS:main() token=" + token);
        String apiHost = args[0];
        String cmdbHost = args[1];
        String itsmHost = args[2];

        System.out.println("apiHost = " + apiHost);

        String fileWorkloads = null;
        String fileCustomColumns = null;
        String fileCmdb = "cmdbs.json";
        JsonArray jaCMDBs = null;
        JsonArray jaCColumns = null;
        JsonArray jaWorkloads = null;

        if (bouchon) {
            fileWorkloads = "workloads.json";
            jaWorkloads = Util.readFileData(fileWorkloads);
            jaCMDBs = Util.readFileData("cmdbs.json");
            jaCColumns = Util.readFileData("customColumns.json");

        } else {
            String idt = DateTimeFormatter.ofPattern("yyMMdd_HHmmss")
                    .withZone(ZoneId.of("Europe/Paris"))
                    .format(Instant.now());

            fileWorkloads = "workloads/" + idt + "-workloads.json";
            fileCustomColumns = "workloads/" +idt + "-customColumns.json";
            jaWorkloads = Util.download("https://" + apiHost + "/workloads?token=" + token, fileWorkloads);
            jaCMDBs = Util.download("https://" + cmdbHost + "/v2/cmdbs", fileCmdb);
            jaCColumns = Util.download("https://" + apiHost + "/custom_columns?token=" + token, fileCustomColumns);
        }

        CMDBs cmdbs = new CMDBs(jaCMDBs);
        CustomColumnIDs customColumnIDs = new CustomColumnIDs(jaCColumns);

        try {

            BufferedReader WLreader = new BufferedReader(new FileReader(fileWorkloads));
            String line = WLreader.readLine();
            while (line != null) {
                if (debug)
                    System.out.println("Processing line: " + line);
                
                JsonObject jWorkload = JsonParser.parseString(line).getAsJsonObject();
                Workload workload = new Workload(jWorkload);
                int recordID = workload.getId();
                if (debug)
                    System.out.println("recordID = " + recordID);
                CustomColumns customColumns = workload.getCustomColumns();
                CMDB cmdb = cmdbs.getCMDB(workload.getClusterName(), workload.getNamespace());

                String charged = workload.getContainers().getCharged();
                if (debug)
                    System.out.println("charged = " + charged);

                String annotations = workload.getAnnotations();
                if (debug)
                    System.out.println("annotations = " + annotations);

                String ticketID = (String) customColumns.getCustomColumnValue(Constants.COLUMN_SN_TICKET);
                if (debug)
                    System.out.println("ticketID = " + ticketID);

                String samStatus = (String) customColumns
                        .getCustomColumnValue(Constants.COLUMN_SAM_STATUS);
                if (debug)
                    System.out.println("samStatus = " + samStatus);

                String remediationNeeded = (String) customColumns
                        .getCustomColumnValue(Constants.COLUMN_SAM_REMEDIATION_NEEDED);
                if (debug)
                    System.out.println("remediationNeeded = " + remediationNeeded);

                if (remediationNeeded != null && remediationNeeded.equalsIgnoreCase("yes")) {
                    // SAM asks action on the ticket
                    if (debug)
                        System.out.println("Remediation Needed , need ticket action");

                    if (ticketID == null) {
                        // Create a new ticket if it doesn't exist
                        if (debug)
                            System.out.println("ticketId=null, no ticket, need to create one");
                        String comment = (String) customColumns.getCustomColumnValue(Constants.COLUMN_COMMENT);
                        if (debug)
                            System.out.println("comment = " + comment);
                        if (comment == null) {
                            comment = "No annotation or wrong annotation(s)";
                        }

                        // Ticket Creation
                        Incident incident = new Incident("https://"+itsmHost+"/v2/incident", token, cmdb.getOwnerEmail(), cmdb.getProject(),
                                comment);
                        // update customColumns in workload table License Reporter
                        Util.persist_data_customColumns(apiHost, token, recordID,
                                customColumnIDs.getColumnID(Constants.COLUMN_SN_TICKET), incident.getTid());
                        Util.persist_data_customColumns(apiHost, token, recordID,
                                customColumnIDs.getColumnID(Constants.COLUMN_PROJECT), cmdb.getProject());
                        Util.persist_data_customColumns(apiHost, token, recordID,
                                customColumnIDs.getColumnID(Constants.COLUMN_COMMENT), incident.getComment());
                        Util.persist_data_customColumns(apiHost, token, recordID,
                                customColumnIDs.getColumnID(Constants.COLUMN_OWNER), cmdb.getOwnerEmail());
                        Util.persist_data_customColumns(apiHost, token, recordID,
                                customColumnIDs.getColumnID(Constants.COLUMN_SN_STATUS), incident.getStatus());
                        Util.persist_data_customColumns(apiHost, token, recordID,
                                customColumnIDs.getColumnID(Constants.COLUMN_RECID), String.valueOf(recordID));

                        Util.persist_data_customColumns(apiHost, token, recordID,
                                customColumnIDs.getColumnID(Constants.COLUMN_DATE), incident.getOpened_date());
                        Util.persist_data_customColumns(apiHost, token, recordID,
                                customColumnIDs.getColumnID(Constants.COLUMN_CHARGED),
                                charged);
                        Util.clear_data_customColumns(apiHost, token, recordID,
                                customColumnIDs.getColumnID(Constants.COLUMN_SAM_REMEDIATION_NEEDED));
                        if ( samStatus != null) {
                            Util.clear_data_customColumns(apiHost, token, recordID,
                                customColumnIDs.getColumnID(Constants.COLUMN_SAM_STATUS));
                            }

                    } else {
                        // ASM asks the ticket to be reopened, if the ticket is already created, the
                        // custom columns in Reporter are already updated
                        if (debug)
                            System.out.println("ticketId!=null, ticket exists, need to s et it to Open");
                        Incident incident = new Incident("https://"+itsmHost+"/v2/incident",ticketID);
                        incident.syncStatus("Open");
                        Util.persist_data_customColumns(apiHost, token, recordID,
                                customColumnIDs.getColumnID(Constants.COLUMN_SAM_STATUS), "Open");
                        Util.clear_data_customColumns(apiHost, token, recordID,
                                customColumnIDs.getColumnID(Constants.COLUMN_SAM_REMEDIATION_NEEDED));
                    }
                } else if (annotations == null && charged.equals("true")) {
                    // If annotations are missing and if it is charged we need to warn ASM
                    // (samStatus = "Open")
                    // need to know what to do when the parameter charged in a container is equal to
                    // null
                    // for the moment only when charged = "true"
                    if (debug)
                        System.out.println("Remediation not Needed, annotations null and charged");

                    Util.persist_data_customColumns(apiHost, token, recordID,
                            customColumnIDs.getColumnID(Constants.COLUMN_PROJECT), cmdb.getProject());
                    Util.persist_data_customColumns(apiHost, token, recordID,
                            customColumnIDs.getColumnID(Constants.COLUMN_OWNER), cmdb.getOwnerEmail());
                    Util.persist_data_customColumns(apiHost, token, recordID,
                            customColumnIDs.getColumnID(Constants.COLUMN_CHARGED),
                            workload.getContainers().getCharged());
                    Util.persist_data_customColumns(apiHost, token, recordID,
                            customColumnIDs.getColumnID(Constants.COLUMN_RECID), String.valueOf(recordID));
                    if (debug)
                        System.out.println("persist Sam Status to Open");
                    Util.persist_data_customColumns(apiHost, token, recordID,
                            customColumnIDs.getColumnID(Constants.COLUMN_SAM_STATUS), "Open");


                } else {
                    // No need of Remediation , just enrich from CMDB
                    if (debug)
                        System.out.println("No Remediation Needed, annotations not null or not charged");
                    Util.persist_data_customColumns(apiHost, token, recordID,
                            customColumnIDs.getColumnID(Constants.COLUMN_PROJECT), cmdb.getProject());
                    Util.persist_data_customColumns(apiHost, token, recordID,
                            customColumnIDs.getColumnID(Constants.COLUMN_OWNER), cmdb.getOwnerEmail());
                    Util.persist_data_customColumns(apiHost, token, recordID,
                            customColumnIDs.getColumnID(Constants.COLUMN_CHARGED), charged);
                    Util.persist_data_customColumns(apiHost, token, recordID,
                            customColumnIDs.getColumnID(Constants.COLUMN_RECID), String.valueOf(recordID));
                    if ( samStatus != null  ) {
                        if (debug)
                            System.out.println("Clear Sam Status as no remediation is needed"); 
                        Util.clear_data_customColumns(apiHost, token, recordID,
                            customColumnIDs.getColumnID(Constants.COLUMN_SAM_STATUS));
                    }
                }

                if (ticketID != null) {
                    // the ticket exist in memory. We need to synchronise with the ITSM content
                    if (debug)
                        System.out.println("TicketID != null, need to sync with ITSM");
                    Incident incident = new Incident("https://"+itsmHost+"/v2/incident", ticketID);
                    String status = incident.getStatus();
                    if (status == null) {
                        // the ticket has been erased in the ITSM, we need to erase the custom columns
                        // in the workload table
                        if (debug)
                            System.out.println("Ticket has been erased in ITSM, need to clear custom columns");
                        Util.clear_data_customColumns(apiHost, token, recordID,
                                customColumnIDs.getColumnID(Constants.COLUMN_SN_TICKET));
                        Util.clear_data_customColumns(apiHost, token, recordID,
                                customColumnIDs.getColumnID(Constants.COLUMN_SN_STATUS));
                        Util.clear_data_customColumns(apiHost, token, recordID,
                                customColumnIDs.getColumnID(Constants.COLUMN_COMMENT));
                        Util.clear_data_customColumns(apiHost, token, recordID,
                                customColumnIDs.getColumnID(Constants.COLUMN_SAM_STATUS));
                    } else {
                        // the ticket still exists in the ITSM, we need to update ticket status in the
                        // workload table (Open|Closed)
                        if (debug)
                            System.out.println("Ticket exists in ITSM, need to update status in custom columns");   
                        Util.persist_data_customColumns(apiHost, token, recordID,
                                customColumnIDs.getColumnID(Constants.COLUMN_SN_STATUS),
                                status);    
                    }

                }
                // Read the next line

                line = WLreader.readLine();
            }
            WLreader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}