package com.herve.gateway;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter({ "/cmdb*", "/web*", "/incident*", "*.xhtml" })
public class JwtFilter implements Filter {

    private static final LogManager logManager = LogManager.getLogManager();
    private static final Logger LOGGER = Logger.getLogger(JwtFilter.class.getName());

    static {
        try {
            logManager.readConfiguration(JwtFilter.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load logging configuration", e);
        }
    }
    private @Inject OidcConfig oidcConfig;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        LOGGER.info("In JwtFilter, path: " + request.getRequestURI());

        Enumeration<String> parameterNames = request.getParameterNames();

        String code = request.getParameter("code");
        if (code != null) {
            LOGGER.info("Received authorization code: " + code);
        } else {
            LOGGER.info("No authorization code found in request parameters.");
        }

        String session_state = request.getParameter("session_state");
        if (session_state != null) {
            LOGGER.info("Received session state: " + session_state);
        } else {
            LOGGER.info("No session state found in request parameters.");
        }

        HttpSession session = request.getSession();
        if (session == null) {
            LOGGER.warning("No session found, creating a new one.");
            session = request.getSession(true);
        } else {
            LOGGER.info("Session ID: " + session.getId());
        }

        String accessToken = null;

        if (code == null || session_state == null) {
            LOGGER.info("code = null, or session_state = null, checking if they are stored in the session attributes.");
            code = (String) session.getAttribute("code");
            session_state = (String) session.getAttribute("session_state");
            accessToken = (String) session.getAttribute("accessToken");
        } else {
            LOGGER.info("code and session_state are not null, storing them in the session attributes with access token.");
            String redirectUri = request.getRequestURL().toString();
            LOGGER.info("Redirect URI: " + redirectUri);

            String targetUrl = oidcConfig.getInternalIssuerUri() + "/protocol/openid-connect/token";
            LOGGER.info("Target URL for token exchange: " + targetUrl);

            String urlEncodedPayload = "client_id=" + oidcConfig.getClientId() +
                    "&client_secret=" + oidcConfig.getClientSecret() +
                    "&redirect_uri=" + redirectUri +
                    "&code=" + code +
                    "&grant_type=authorization_code";
            LOGGER.info("URL-encoded payload for token exchange: " + urlEncodedPayload);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(targetUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(urlEncodedPayload))
                    .build();
            HttpResponse<String> resp;

            try {
                resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                LOGGER.info("Response status code: " + resp.statusCode());
                if (resp.statusCode() == 200) {
                    JsonReader jsonReader = Json.createReader(new StringReader(resp.body()));
                    JsonObject jsonResp = jsonReader.readObject();
                    accessToken = jsonResp.getString("access_token");
                    LOGGER.info("Access token received: " + accessToken);
                    LOGGER.info("Storing code, session_state, and accessToken in session attributes.");
                    session.setAttribute("code", code);
                    session.setAttribute("session_state", session_state);
                    session.setAttribute("accessToken", accessToken);
                } else {
                    session.invalidate();
                    LOGGER.info("Failed to retrieve access token, response code: " + resp.statusCode());
                    LOGGER.info("session is invalid");
                    code = null;
                    session_state = null;
                }

            } catch (

            IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (accessToken == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getOutputStream().print("Unauthorized");
            return;
        }
        chain.doFilter(request, response);

    }

    @Override
    public void destroy() {
    }

}
