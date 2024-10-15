// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017, 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
// end::copyright[]
package com.herve.petstore;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.herve.soap.InventoryItem;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.stream.JsonParser;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/")
public class PetStore_user {
	
    @Inject
    private UserDao userDAO;

	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("users")
    @Transactional
    public List<User> getUsers() {
    	return userDAO.readAllUsers();
   }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user/{user}")
    @Transactional
    public User getUser(@DefaultValue("herve") @PathParam("user") String username ) {
   
    	User user = userDAO.readUser(username);
    	if (user != null) {
            return user;
        }
     	return null;
    } 
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("user")
    @Transactional
      public int createUser(User user) {
    	 if (!userDAO.findUser(user.getUsername()).isEmpty()) {
             return 0;
         }
        userDAO.createUser(user);
     	return user.getId();
   } 
    
}