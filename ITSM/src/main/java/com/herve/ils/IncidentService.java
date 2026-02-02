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

import java.sql.SQLException;
import java.util.List;
import javax.naming.NamingException;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@RequestScoped
@Path("/")
@Tag(name = "Incident Management", description = "APIs for managing incidents")
public class IncidentService {
	
    @Inject
    private IncidentDao incidentDAO; 
    

	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("incidents")
    @Transactional
    @Operation(summary = "Get all incidents", description = "Retrieves a list of all incidents in the system")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Successfully retrieved incidents",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Incident.class)))
    })
    public List<Incident> getIncidents() throws SQLException, NamingException {
    	IncidentList il = new IncidentList();
    	//return il.getIncidentList();
   // 	il.setIncidentList(incidentDAO.readAllIncidents());
    	//return incidentDAO.readAllIncidents();
    	return il.getIncidents();
   }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("incident/{incident}")
    @Transactional
    @Operation(summary = "Get incident by ID", description = "Retrieves a specific incident by its ID")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Successfully retrieved incident",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Incident.class))),
        @APIResponse(responseCode = "404", description = "Incident not found")
    })
    public Incident getIncident(
        @Parameter(description = "Incident ID", required = true, example = "111111")
        @DefaultValue("111111") @PathParam("incident") Long id) throws SQLException, NamingException {
    	IncidentList il = new IncidentList();
    	if ( ! il.getIncidents().isEmpty()) {
          Incident incident = incidentDAO.readIncident(id);
        	if (incident != null) {
              return incident;
        	}
    	}
    	return null;
    } 
    
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("incident")
    @Transactional
    @Operation(summary = "Update incident", description = "Updates an existing incident")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Successfully updated incident"),
        @APIResponse(responseCode = "404", description = "Incident not found")
    })
    public long updateStatusIncident(
        @Parameter(description = "Incident object to update", required = true)
        Incident incident) {
    	if (incidentDAO.findIncident(incident.getId()).isEmpty()) {
            return 0;
        }
        incidentDAO.updateIncident(incident);
     	return incident.getId();
   } 
    
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("incident")
    @Transactional
    @Operation(summary = "Create incident", description = "Creates a new incident")
    @APIResponses(value = {
        @APIResponse(responseCode = "201", description = "Successfully created incident"),
        @APIResponse(responseCode = "409", description = "Incident already exists")
    })
    public long createIncident(
        @Parameter(description = "Incident object to create", required = true)
        Incident incident) {
    	if (!incidentDAO.findIncident(incident.getId()).isEmpty()) {
             return 0;
         }
        incidentDAO.createIncident(incident);
     	return incident.getId();
   } 
    
}