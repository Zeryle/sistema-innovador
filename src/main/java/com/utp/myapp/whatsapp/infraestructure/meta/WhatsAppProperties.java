package com.utp.myapp.whatsapp.infraestructure.meta;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Reads the Meta WhatsApp Cloud API credentials from application.properties.
 *
 * Properties (set them in environment variables or application-{profile}.properties
 * before flipping {@code whatsapp.mode} to "live"):
 *   whatsapp.mode                      = "mock" (default) or "live"
 *   whatsapp.meta.access-token         = the EAAJ... permanent system-user token
 *   whatsapp.meta.phone-number-id      = the 15-digit phone number ID
 *   whatsapp.meta.business-account-id  = the 15-digit WABA ID
 *   whatsapp.meta.api-version          = "v20.0" by default
 *   whatsapp.meta.verify-token         = shared secret for the webhook challenge
 *
 * The "live" implementation (MetaCloudApiAdapter) is wired but is a stub for now:
 * it logs the request that WOULD be sent and returns a deterministic fake message
 * id. To go live, drop in an HTTP client (RestTemplate or WebClient) inside
 * sendTemplate / sendText, and use the credentials below to authenticate.
 */
@Component
@ConfigurationProperties(prefix = "whatsapp.meta")
public class WhatsAppProperties {
    /** "mock" (default) keeps MockWhatsAppGateway as @Primary.
     *  "live" swaps to MetaCloudApiAdapter (production). */
    private String mode = "mock";
    /** Permanent system-user access token from Meta Business Suite. */
    private String accessToken = "";
    /** The 15-digit phone number ID, NOT the actual phone number. */
    private String phoneNumberId = "";
    /** The 15-digit WhatsApp Business Account ID. */
    private String businessAccountId = "";
    /** Graph API version. v20.0 is current as of mid-2026. */
    private String apiVersion = "v20.0";
    /** Shared secret for the GET webhook challenge. */
    private String verifyToken = "auto-taller-demo-verify";

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getPhoneNumberId() { return phoneNumberId; }
    public void setPhoneNumberId(String phoneNumberId) { this.phoneNumberId = phoneNumberId; }
    public String getBusinessAccountId() { return businessAccountId; }
    public void setBusinessAccountId(String businessAccountId) { this.businessAccountId = businessAccountId; }
    public String getApiVersion() { return apiVersion; }
    public void setApiVersion(String apiVersion) { this.apiVersion = apiVersion; }
    public String getVerifyToken() { return verifyToken; }
    public void setVerifyToken(String verifyToken) { this.verifyToken = verifyToken; }

    public boolean isLiveMode() { return "live".equalsIgnoreCase(mode); }
}
