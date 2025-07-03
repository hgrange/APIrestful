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
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.annotation.security.DeclareRoles;
import jakarta.inject.Inject;
import jakarta.security.enterprise.authentication.mechanism.http.OpenIdAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.openid.LogoutDefinition;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@WebServlet("/")
@OpenIdAuthenticationMechanismDefinition(
  providerURI = "${oidcConfig.issuerUri}", 
  clientId = "${oidcConfig.clientId}",
  clientSecret = "${oidcConfig.clientSecret}",
  jwksConnectTimeout = 5000,
  jwksReadTimeout = 5000,
  redirectToOriginalResource = true, 
  logout = @LogoutDefinition(notifyProvider = true))

@DeclareRoles({ "admin", "user" })
public class AuthResource extends HttpServlet {

private @Inject OidcConfig oidcConfig;

private static final Logger LOGGER = Logger.getLogger(AuthResource.class.getName());

  private static final long serialVersionUID = 1L;
 /*public void init(ServletConfig servletConfig) {
        oidcConfig = new OidcConfig();
   }*/
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    LOGGER.info("Method doGet:");
    Enumeration<String> headerNames = request.getHeaderNames();
    Enumeration<String> parameterNames = request.getParameterNames();

    String code = request.getParameter("code");
    String session_state = request.getParameter("session_state");
    if (code != null && session_state != null) {
      HttpSession session = request.getSession();
      session.setAttribute("code", code);
      session.setAttribute("session_state", session_state);
    } 
    String url = request.getRequestURL().toString();
    response.sendRedirect(
        oidcConfig.getIssuerUri()+"/protocol/openid-connect/auth?client_id=" +
        oidcConfig.getClientId()+"&response_mode=form_post&response_type=code&login=true"+
        "&redirect_uri="+url+"incident.xhtml");
       
  }
}
