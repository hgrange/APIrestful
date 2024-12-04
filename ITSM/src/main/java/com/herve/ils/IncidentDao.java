package com.herve.ils;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
@Named
public class IncidentDao  {
	List<Incident> lIncidents;
	
		
    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;

    public void createIncident(Incident incident) {
        em.persist(incident);
    }

    public Incident readIncident(Long id) {
    	Incident incident = em.find(Incident.class, id);
        return incident;
    }

    public void updateIncident(Incident incident) {
        em.merge(incident);
    }

    public void deleteIncident(Incident incident) {
    	Incident inc= em.merge(incident);
        em.remove(inc);
    }

    public List<Incident> readAllIncidents() {
    	lIncidents= em.createNamedQuery("Incident.findAll", Incident.class).getResultList();
    	return lIncidents;
    }
    

    public List<Incident> findIncident(Long id) {
        lIncidents= em.createNamedQuery("Incident.findIncident", Incident.class)
            .setParameter("id", id).getResultList();
        return lIncidents;

    }
}
