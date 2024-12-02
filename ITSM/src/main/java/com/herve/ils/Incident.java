package com.herve.ils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity 
@Table(name="INCIDENTS")
@NamedQuery(name = "Incident.findAll", query = "SELECT i FROM Incident i")
@NamedQuery(name = "Incident.findIncident", query = "SELECT i FROM Incident i WHERE "
    + "i.id = :id ")
public class Incident {
    @Column(name="CHECKED", columnDefinition="BOOLEAN") boolean checked;
	@Id
	@Column(name="ID") Long id;
	@Column(name="TITLE") String title;
	@Column(name="DESCRIPTION") String description;
	@Column(name="PROJECT")String project;
	@Column(name="OEMAIL")String ownerEmail;
	@Column(name="ODATE")String openingDate;
	@Column(name="CDATE")String closedDate;
	@Column(name="ISTATUS") String status;

	
	public Incident() {
	}
	
	Incident(boolean checked, Long id, String title, String description, String project, String ownerEmail, String openingDate, String closedDate, String status) {
		setId(id);
		setDescription(description);
		setProject(project);
		setOwnerEmail(ownerEmail);
		setOpeningDate(openingDate);
		setClosedDate(closedDate);
		setStatus(status);
		setChecked(checked);
	}
	
	@Override
    public boolean equals(Object object) {
        return (object instanceof Incident) && (id != null) 
             ? id.equals(((Incident) object).id) 
             : (object == this);
    }

	@Override
    public int hashCode() {
        return (id != null)
             ? (Incident.class.hashCode() + id.hashCode())
             : super.hashCode();
    }
	
	
	public Long getId() {
		return id;
    }
	
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getOwnerEmail() {
		return ownerEmail;
	}
	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}
	public String getOpeningDate() {
		return openingDate;
	}
	public void setOpeningDate(String openingDate) {
		this.openingDate = openingDate;
	}
	public String getClosedDate() {
		return closedDate;
	}
	public void setClosedDate(String closedDate) {
		this.closedDate = closedDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public boolean getChecked() {
		return checked;
	}
	
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	
	
	public String toString() {
        return "Incident [ checked="+getChecked()+"id="+getId()+", Description="+getDescription()+
        		", project="+getProject()+", ownerEmail="+getOwnerEmail()+", openingDate="+getOpeningDate()+", closedDate="+getClosedDate()+
        		", incidentStatus="+getStatus();
                
    }

}
