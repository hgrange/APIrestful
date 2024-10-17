package com.herve.petstore;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RequestScoped
public class UserDao {

   
    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;

    public void createUser(User user) {
        em.persist(user);
    }

    public User readUser(String username) {
    	User user = em.find(User.class, username);
    	user.setPassword("*******");
        return user;
    }

    public void updateUser(User user) {
        em.merge(user);
    }

    public void deleteUser(User user) {
        em.remove(user);
    }

    public List<User> readAllUsers() {
        List<User> lUsers= em.createNamedQuery("User.findAll", User.class).getResultList();
 /*       for (User user : lUsers ) {
        	user.setPassword("*******");
        } */
    	return lUsers;
    }
    

    public List<User> findUser(String username) {
        List<User> lUsers= em.createNamedQuery("User.findUser", User.class)
            .setParameter("username", username).getResultList();
  /*      for (User user : lUsers ) {
        	user.setPassword("*******");
        }*/
        return lUsers;

    }
}
