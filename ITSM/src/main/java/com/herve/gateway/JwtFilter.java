package com.herve.gateway;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.Normalizer.Form;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@WebFilter({ "/cmdb*", "/web*", "/incident*", "*.xhtml" })
public class JwtFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(JwtFilter.class.getName());

    private @Inject OidcConfig oidcConfig;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        LOGGER.info("In JwtFilter, path: " + request.getRequestURI());

        Enumeration<String> parameterNames = request.getParameterNames();

        String code = request.getParameter("code");
        String session_state = request.getParameter("session_state");
        HttpSession session = request.getSession();
        String accessToken = null;

        if (code == null || session_state == null) {
            code = (String) session.getAttribute("code");
            session_state = (String) session.getAttribute("session_state");
            accessToken = (String) session.getAttribute("accessToken");
        } else {

            String redirectUri = request.getRequestURL().toString();
            String targetUrl = oidcConfig.getIssuerUri() + "/protocol/openid-connect/token";

            String urlEncodedPayload = "client_id=" + oidcConfig.getClientId() +
                    "&client_secret=" + oidcConfig.getClientSecret() +
                    "&redirect_uri=" + redirectUri +
                    "&code=" + code +
                    "&grant_type=authorization_code";

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
                if (resp.statusCode() == 200) {
                    JsonReader jsonReader = Json.createReader(new StringReader(resp.body()));
                    JsonObject jsonResp = jsonReader.readObject();
                    accessToken = jsonResp.getString("access_token");
                    session.setAttribute("code", code);
                    session.setAttribute("session_state", session_state);
                    session.setAttribute("accessToken", accessToken);
                } else {
                    session.invalidate();
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
