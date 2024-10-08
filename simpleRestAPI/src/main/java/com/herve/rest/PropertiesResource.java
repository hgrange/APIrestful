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
package com.herve.rest;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.stream.JsonParser;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("properties")
public class PropertiesResource {  
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Properties getProperties() {
    // public JsonObject getProperties() {
    JsonObject readObject = null;
		try {
			ClassLoader myclass = getClass().getClassLoader();
			URL resource = getClass().getClassLoader().getResource("BackupData.json");
			
			//JsonReader jsonReader = Json.createReader(new StringReader(Files.readString(Paths.get(resource.toURI()))));
		    // readObject = jsonReader.readObject();
	/*	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); */
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 

        return System.getProperties();
	//return readObject;
    }

    

}
