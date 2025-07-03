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
package com.herve.ils;

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
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@RequestScoped
@Path("/")
public class CMDBService {
	
    @Inject
    private CMDBDao cmdbDAO;

   private static final LogManager logManager = LogManager.getLogManager();
   private static final Logger LOGGER = Logger.getLogger(CMDBService.class.getName());

	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("cmdbs")
    @Transactional
    public List<CMDB> getCMDBs() {
    	return cmdbDAO.readAllCMDBs();
   }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("cmdb/{cmdb}")
    @Transactional
    public CMDB getCMDB(@DefaultValue("cluster1_namespace1") @PathParam("cmdb") String id ) {
   
    	CMDB cmdb = cmdbDAO.readCMDB(id);
    	if (cmdb != null) {
            return cmdb;
        }
     	return null;
    } 
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("cmdb")
    @Transactional
      public String createCMDB(CMDB cmdb) {
    	
    	 if (!cmdbDAO.findCMDB(cmdb.getSid()).isEmpty()) {
             return cmdb.getSid();
         }
        cmdbDAO.createCMDB(cmdb);
     	return cmdb.getSid();
   } 
    
}
