package com.herve.ils;

import java.util.List;


import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RequestScoped
public class CMDBDao {

   
    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;

    public void createCMDB(CMDB cmdb) {
        em.persist(cmdb);
    }

    public CMDB readCMDB(String sid) {
    	CMDB cmdb = em.find(CMDB.class, sid);
        return cmdb;
    }

    public void updateCMDB(CMDB cmdb) {
        em.merge(cmdb);
    }

    public void deleteCMDB(CMDB cmdb) {
        em.remove(cmdb);
    }

    public List<CMDB> readAllCMDBs() {
        List<CMDB> lCMDBs= em.createNamedQuery("CMDB.findAll", CMDB.class).getResultList();
    	return lCMDBs;
    }
    

    public List<CMDB> findCMDB(String sid) {
        List<CMDB> lCMDBs= em.createNamedQuery("CMDB.findCMDB", CMDB.class)
            .setParameter("sid", sid).getResultList();
        return lCMDBs;

    }
}
