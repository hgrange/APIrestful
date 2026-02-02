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

import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.herve.RestApplication;

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
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@RequestScoped
@Path("/")
@Tag(name = "CMDB Management", description = "APIs for managing Configuration Management Database items")
public class CMDBService {
    private static final LogManager logManager = LogManager.getLogManager();
    private static final Logger LOGGER = Logger.getLogger(CMDBService.class.getName());
    static {
        try {
            logManager.readConfiguration(CMDBService.class.getResourceAsStream("/logging.properties"));
        } catch (Exception e) {
            LOGGER.severe("Failed to load logging configuration: " + e.getMessage());
        }
    }
    @Inject
    private CMDBDao cmdbDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/cmdbs")
    @Transactional
    @Operation(summary = "Get all CMDB items", description = "Retrieves a list of all configuration items in the CMDB")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Successfully retrieved CMDB items",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CMDB.class)))
    })
    public List<CMDB> getCMDBs() {
         LOGGER.info("Method getCMDBs():");
        return cmdbDAO.readAllCMDBs();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("cmdb/{cmdb}")
    @Transactional
    @Operation(summary = "Get CMDB item by ID", description = "Retrieves a specific configuration item by its ID")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Successfully retrieved CMDB item",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CMDB.class))),
        @APIResponse(responseCode = "404", description = "CMDB item not found")
    })
    public CMDB getCMDB(
        @Parameter(description = "CMDB item ID", required = true, example = "cluster1_namespace1")
        @DefaultValue("cluster1_namespace1") @PathParam("cmdb") String id) {

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
    @Operation(summary = "Create CMDB item", description = "Creates a new configuration item in the CMDB")
    @APIResponses(value = {
        @APIResponse(responseCode = "201", description = "Successfully created CMDB item"),
        @APIResponse(responseCode = "409", description = "CMDB item already exists")
    })
    public String createCMDB(
        @Parameter(description = "CMDB object to create", required = true)
        CMDB cmdb) {

        if (!cmdbDAO.findCMDB(cmdb.getSid()).isEmpty()) {
            return cmdb.getSid();
        }
        cmdbDAO.createCMDB(cmdb);
        return cmdb.getSid();
    }

}