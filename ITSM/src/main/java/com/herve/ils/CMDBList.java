package com.herve.ils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.herve.ils.Incident;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.Column;
import jakarta.transaction.Transactional;

@Named
@ApplicationScoped
public class CMDBList {
	
	  @Inject
	    private IncidentDao incidentDAO; 
	
	

	private List<CMDB> cmdbs = new ArrayList<CMDB>();
	
	
	private Map<Long, Boolean> checked = new HashMap<Long, Boolean>();
	
	
	CMDBList() throws SQLException, NamingException {
		InitialContext ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("jdbc/ds1");
	
		Connection conn = ds.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select * from CMDB");
		 while(rs.next())
         {
			 String sid = rs.getString("SID");
			 String cluster = rs.getString("CLUSTER");
			 String namespace = rs.getString("NAMESPACE");
			 String project = rs.getString("PROJECT");
			 String ownerEmail = rs.getString("OEMAIL");
			
			 
			 CMDB cmdb = new CMDB(sid, cluster, namespace, project, ownerEmail);
			 cmdbs.add(cmdb);
         }

	}

	
	public List<CMDB> getCmdbs() {
		return cmdbs;
	}
	
	public void setCmdbs(List cmdbs) {
		this.cmdbs = cmdbs;	
	}
	
}
