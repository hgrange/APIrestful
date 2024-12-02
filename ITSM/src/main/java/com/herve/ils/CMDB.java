package com.herve.ils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity 
@Table(name="CMDB")
@NamedQuery(name = "CMDB.findAll", query = "SELECT c FROM CMDB c")
@NamedQuery(name = "CMDB.findCMDB", query = "SELECT c FROM CMDB c WHERE "
    + "c.sid = :sid ")
public class CMDB {

	@Id
	@Column(name="SID") String sid;
	@Column(name="CLUSTER") String cluster;
	@Column(name="NAMESPACE") String namespace;
	@Column(name="PROJECT")String project;
	@Column(name="OEMAIL")String ownerEmail;
	
	
	public CMDB() {
	}
	
	CMDB(String sid, String cluster, String namespace, String project, String ownerEmail) {
		setSid(sid);
		setCluster(cluster);
		setNamespace(namespace);
		setProject(project);
		setOwnerEmail(ownerEmail);
	}
	
	public String getSid() {
		return sid;
    }
	
	public void setSid(String sid) {
		this.sid = sid;
	}
	
	public String getCluster() {
		return cluster;
	}
	public void setCluster(String cluster) {
		this.cluster = cluster;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getNamespace() {
		return namespace;
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
	public String toString() {
        return "CMDB [ id="+getSid()+", cluster="+getCluster()+", namespace="+getNamespace()+
        		", project="+getProject()+", ownerEmail="+getOwnerEmail();
                
    }

}
