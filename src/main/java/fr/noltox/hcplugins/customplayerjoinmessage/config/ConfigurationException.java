package fr.noltox.hcplugins.customplayerjoinmessage.config;

import java.io.Serial;

/**
 * Signals an invalid or unreadable join-message configuration.
 */
public final class ConfigurationException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
