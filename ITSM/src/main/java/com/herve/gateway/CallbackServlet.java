package com.herve.gateway;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

import jakarta.inject.Inject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.security.enterprise.identitystore.openid.OpenIdContext;
import jakarta.security.enterprise.authentication.mechanism.http.openid.OpenIdConstant;

@WebServlet("/callback2")
public class CallbackServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CallbackServlet.class.getName());
    @Inject
    private OpenIdContext context;


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        if (context != null) {
            Optional<String> originalRequest = context.getStoredValue(request, response, OpenIdConstant.ORIGINAL_REQUEST);
            String originalRequestString = originalRequest.get();
            response.sendRedirect(originalRequestString);
        }
    }
}
