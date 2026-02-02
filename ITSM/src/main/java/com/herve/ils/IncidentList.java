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
import java.util.stream.Collectors;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

@Named
@RequestScoped
public class IncidentList {
	
	  @Inject
	    private IncidentDao incidentDAO; 
	  
	  InitialContext ctx = new InitialContext();
	  DataSource ds = (DataSource)ctx.lookup("jdbc/ds1");
	
	//ArrayList<Incident> iList = new ArrayList<Incident>();
	private List<Incident> incidents; 
	private String filter;
	
	private Map<Long, Boolean> checked = new HashMap<Long, Boolean>();
	
	
	IncidentList() throws SQLException, NamingException {
          incidents = loadList();
	}
	
	public List<Incident> loadList() throws NamingException, SQLException {
		List<Incident> iList = new ArrayList<Incident>();
		
	
		Connection conn = ds.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select * from Incidents");
		 while(rs.next())
         {
			 boolean checked = rs.getBoolean("CHECKED");
			 Long id = rs.getLong("ID");
			 String title = rs.getString("TITLE");
			 String description = rs.getString("DESCRIPTION");
			 String projet = rs.getString("PROJECT");
			 String ownerEmail = rs.getString("OEMAIL");
			 String openingDate = rs.getString("ODATE");
			 String closedDate = rs.getString("CDATE");
			 String status = rs.getString("ISTATUS");
			 
			 Incident incident = new Incident(checked, id, title, description, projet, ownerEmail,
					 openingDate, closedDate, status);
			 iList.add(incident);
         }
		 conn.close();
		 return iList;
	}

	@Transactional
	public void changeState() {

		for (Incident inc: incidents ) {
          if ( inc.getChecked() ) {		
        	int icomp = inc.getStatus().compareToIgnoreCase("Open");
			if ( icomp == 0 ) {
				inc.setStatus("Closed");
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
				LocalDateTime now = LocalDateTime.now();    
				inc.setClosedDate(dtf.format(now));
			} else {
				inc.setStatus("Open");
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
				LocalDateTime now = LocalDateTime.now();     
				inc.setOpeningDate(dtf.format(now));
				inc.setClosedDate(" ");
			}
			inc.setChecked(false);
			incidentDAO.updateIncident(inc);
          }
		}
		
    }
	
	@Transactional
	public void delete() throws NamingException, SQLException {
     
		for (Incident inc: incidents ) {
          if ( inc.getChecked() ) {		
        	inc.setChecked(false);  
        	incidentDAO.deleteIncident(inc);
          }
		}
    }
	
	public void refresh() {
		try {
			incidents = loadList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public void init() throws SQLException {
  	
		Connection conn = ds.getConnection();
		Statement stmt = conn.createStatement();
		int res = stmt.executeUpdate("truncate table Incidents");
		
		conn.close();
		incidents.clear();
		incidentDAO.clear();
		System.out.println("End method init");
	}

	public List<Incident> getIncidents() throws NamingException, SQLException {
		incidents = loadList();
		return incidents;
	}
	
	public void setIncidents(List<Incident> incidents) {
		this.incidents = incidents;
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

	public List<Incident> getFilteredIncidents() throws NamingException, SQLException {
		if (filter == null || filter.trim().isEmpty()) {
			return getIncidents();
		}
		String f = filter.toLowerCase();
		return getIncidents().stream()
				.filter(i -> (String.valueOf(i.getId()).toLowerCase().contains(f))
						|| (i.getDescription() != null && i.getDescription().toLowerCase().contains(f))
						|| (i.getProject() != null && i.getProject().toLowerCase().contains(f))
						|| (i.getOwnerEmail() != null && i.getOwnerEmail().toLowerCase().contains(f))
						|| (i.getStatus() != null && i.getStatus().toLowerCase().contains(f)))
				.collect(Collectors.toList());
	}
 
	public Map<Long, Boolean> getChecked () {
		return checked;
	}
	
	public void setChecked(Map<Long, Boolean> checked) {
		this.checked = checked;
	}
}
