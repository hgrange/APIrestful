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
        return em.find(User.class, username);
    }

    public void updateUser(User user) {
        em.merge(user);
    }

    public void deleteUser(User user) {
        em.remove(user);
    }

    public List<User> readAllUsers() {
        return em.createNamedQuery("User.findAll", User.class).getResultList();
    }

    public List<User> findUser(String username) {
        return em.createNamedQuery("User.findUser", User.class)
            .setParameter("username", username).getResultList();

    }
}
