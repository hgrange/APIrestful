package com.herve.ils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class CMDBList {
	
	  @Inject
	    private IncidentDao incidentDAO; 
	
	

	private List<CMDB> cmdbs = new ArrayList<CMDB>();
	private String filter;
	
	
	private Map<Long, Boolean> checked = new HashMap<Long, Boolean>();
	
	
	CMDBList() throws SQLException, NamingException {
		 cmdbs = loadList();
	}
	
	public List<CMDB> loadList() throws NamingException, SQLException {
		
		List<CMDB> iList = new ArrayList<CMDB>();
		
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
			 iList.add(cmdb);
         }
		 conn.close();
		 System.out.println("iList = "+iList.toString());
		 return iList;
	}
	
	public List<CMDB> getCmdbs() throws NamingException, SQLException {
		cmdbs = loadList();
		return cmdbs;
	}
	
	public void setCmdbs(List cmdbs) {
		this.cmdbs = cmdbs;	
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public void clearFilter() {
		this.filter = null;
	}

	public void refresh() {
		try {
			cmdbs = loadList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<CMDB> getFilteredCmdbs() throws NamingException, SQLException {
		if (filter == null || filter.trim().isEmpty()) {
			return getCmdbs();
		}
		String f = filter.toLowerCase();
		return getCmdbs().stream()
				.filter(c -> (c.getSid() != null && c.getSid().toLowerCase().contains(f))
						|| (c.getCluster() != null && c.getCluster().toLowerCase().contains(f))
						|| (c.getNamespace() != null && c.getNamespace().toLowerCase().contains(f))
						|| (c.getProject() != null && c.getProject().toLowerCase().contains(f))
						|| (c.getOwnerEmail() != null && c.getOwnerEmail().toLowerCase().contains(f)))
				.collect(Collectors.toList());
	}
}
