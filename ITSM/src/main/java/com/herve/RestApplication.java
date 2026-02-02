
/*******************************************************************************
 * Copyright (c) 2017, 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.herve;

import jakarta.ws.rs.core.Application;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.herve.ils.CMDBService;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/v2")
@OpenAPIDefinition(
    info = @Info(
        title = "ITSM API",
        version = "2.0.0",
        description = "IT Service Management API for managing incidents and CMDB items",
        contact = @Contact(
            name = "ITSM Support",
            email = "support@example.com"
        ),
        license = @License(
            name = "Eclipse Public License 2.0",
            url = "https://www.eclipse.org/legal/epl-2.0/"
        )
    ),
    servers = {
        @Server(url = "/", description = "ITSM API Server")
    }
)
public class RestApplication extends Application {
       
}
