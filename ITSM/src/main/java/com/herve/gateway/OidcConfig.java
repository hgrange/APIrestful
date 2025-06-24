package com.herve.gateway;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Named("oidcConfig")
public class OidcConfig {

    private static final Logger LOGGER = Logger.getLogger(OidcConfig.class.getName());

    private String domain;
    private String clientId;
    private String clientSecret;
    private String issuerUri;
    private String internalIssuerUri;

    
    

    OidcConfig() {
        try {
            var properties = new Properties();
            properties.load(getClass().getResourceAsStream("/oidc.properties"));
            clientId = properties.getProperty("clientId");
            clientSecret = properties.getProperty("clientSecret");
            issuerUri = properties.getProperty("issuerUri");
            internalIssuerUri = properties.getProperty("internalIssuerUri");
           
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load oidc.properties", e);
        }
    }

    public String getDomain() {
        return domain;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getIssuerUri() {
        return issuerUri;
    }
    public String getInternalIssuerUri() {
        if (internalIssuerUri == null || internalIssuerUri.isEmpty()) {
            LOGGER.warning("Internal issuer URI is not set in oidc.properties");
            return issuerUri; // Fallback to the main issuer URI if internal is not set
        }
        LOGGER.info("getInternalIssuerUri() Using internal issuer URI: " + internalIssuerUri);
        return internalIssuerUri;
    }

}
