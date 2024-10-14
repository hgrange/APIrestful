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
import java.util.Properties;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.stream.JsonParser;
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

@Path("pet")
public class PetStore {

 
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("findByStatus")

    public JsonArray findByStatus(@DefaultValue("sold") @QueryParam("status") String status) {
    	    JsonArray jsonArray = null;
    	    String fileName;
    	    
    	    if ( status.equals("sold") ) {
    	    	fileName = "PetStoreFindByStatus-Sold.json";
    	    	System.out.println("fileName = " + fileName);
    	    } else {
    	    	return null;
    	    }
    	    
    		ClassLoader myclass = getClass().getClassLoader();
			URL resource = getClass().getClassLoader().getResource(fileName);
			System.out.println("URL resource = " + resource);
			String sFile = resource.toString().replaceFirst("wsjar:", "");
			System.out.println("URL resource = "+ resource.toString() );
			URI uri;
			try {
				uri = new URI(sFile);
	
			
				System.out.println("uri = " + uri.toString());
				java.nio.file.Path path = Paths.get(uri);
				String s_path = Files.readString(path);
				
			
				StringReader sr = new StringReader( s_path);
			
				JsonReader jsonReader = Json.createReader(sr);
			
				//JsonReader jsonReader = Json.createReader(new StringReader(Files.readString(Paths.get(resource.toURI()))));
			    jsonArray = jsonReader.readArray();
            } catch (URISyntaxException e) {
		       e.printStackTrace();
    } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
     	return jsonArray;
   }
    
    @POST
    @Path("{var:.+}")
    @Produces(MediaType.APPLICATION_JSON)

    public String post(@Context UriInfo ui, @PathParam("var") String path, @Context HttpHeaders headers) {
        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
        MultivaluedMap<String, String> pathParams = ui.getPathParameters();
        MultivaluedMap<String, String> headersParams = headers.getRequestHeaders();
    	String response = "{ \"data\":1919 }";
        return response;
    }
    
    @PUT
    @Path("{var:.+}")
    @Produces(MediaType.APPLICATION_JSON)

    public String put(@Context UriInfo ui, @PathParam("var") String path, @Context HttpHeaders headers) {
        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
        MultivaluedMap<String, String> pathParams = ui.getPathParameters();
        MultivaluedMap<String, String> headersParams = headers.getRequestHeaders();
    	String response = "{ \"data\":1919 }";
        return response;
    }
}