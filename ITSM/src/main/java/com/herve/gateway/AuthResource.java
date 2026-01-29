/*******************************************************************************
 * Copyright (c) 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.herve.gateway;

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.annotation.security.DeclareRoles;
import jakarta.inject.Inject;
import jakarta.security.enterprise.authentication.mechanism.http.OpenIdAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.openid.LogoutDefinition;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.logging.LogManager;

@WebServlet("/web")
@OpenIdAuthenticationMechanismDefinition(providerURI = "${oidcConfig.issuerUri}", clientId = "${oidcConfig.clientId}", clientSecret = "${oidcConfig.clientSecret}", jwksConnectTimeout = 5000, jwksReadTimeout = 5000, redirectToOriginalResource = true, logout = @LogoutDefinition(notifyProvider = true))

@DeclareRoles({ "admin", "user" })
public class AuthResource extends HttpServlet {

  private @Inject OidcConfig oidcConfig;

  private static final LogManager logManager = LogManager.getLogManager();
  private static final Logger LOGGER = Logger.getLogger(AuthResource.class.getName());
  static {
    try {
      logManager.readConfiguration(AuthResource.class.getResourceAsStream("/logging.properties"));
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Failed to load logging configuration", e);
    }
  }

  private static final long serialVersionUID = 1L;

  /*
   * public void init(ServletConfig servletConfig) {
   * oidcConfig = new OidcConfig();
   * }
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    LOGGER.info("Method doGet:");
    Enumeration<String> headerNames = request.getHeaderNames();

   
      
    Enumeration<String> parameterNames = request.getParameterNames();

    String code = request.getParameter("code");
    LOGGER.info("Code: " + code);
    String session_state = request.getParameter("session_state");
    LOGGER.info("session_state: " + session_state);
    if (code != null && session_state != null) {
      LOGGER.info("code and session_state not null");
      HttpSession session = request.getSession();
      LOGGER.info("Add code and session_state to http session");
      session.setAttribute("code", code);
      session.setAttribute("session_state", session_state);
    }

    String url = request.getRequestURL().toString();
    String serverName = request.getServerName();
    String serverPort = Integer.toString(request.getServerPort());
    String protocol = request.getRequestURL().toString().split("://")[0];
    LOGGER.info("serverName: " + serverName);
    LOGGER.info("serverPort: " + serverPort);
    LOGGER.info("request URL: " + url);
    String redirect_url = url;
    if (!url.endsWith("v2/cmdbs")) {
      if (!url.endsWith("v2/incidents")) {
        if (!url.endsWith("cmdb.xhtml")) {
          if (!url.endsWith("incident.xhtml")) {
            if (!url.endsWith("favicon.ico")) {
              LOGGER.info("URL does not end with v2/cmdb, v2/incidents, cmdb.xhtml, favicon.io or incident.xhtml");
              if (serverPort.equals("80") || serverPort.equals("443")) {
                redirect_url = protocol + "://" + serverName+ "/web/incident.xhtml";
              } else {
                redirect_url = protocol + "://" + serverName + ":" + serverPort + "/web/incident.xhtml";
              }
            }
          }
        }
      }
    }
    // validation tu token OIDC bearer
    // http://localhost:8080/realms/openliberty/protocol/openid-connect/token/introspect"
    LOGGER.info("response.sendRedirect: " + oidcConfig.getIssuerUri() + "/protocol/openid-connect/auth?client_id=" +
        oidcConfig.getClientId() + "&response_mode=form_post&response_type=code&login=true" +
        "&redirect_uri=" + redirect_url);
    response.sendRedirect(
        oidcConfig.getIssuerUri() + "/protocol/openid-connect/auth?client_id=" +
            oidcConfig.getClientId() + "&response_mode=form_post&response_type=code&login=true" +
            "&redirect_uri=" + redirect_url);

  }
}
