package fr.noltox.hcplugins.customplayerjoinmessage.config;

import fr.noltox.hcplugins.core.api.config.BukkitYaml;

import java.nio.file.Path;

/**
 * Reads config.yml before it can replace the live configuration.
 */
public final class ConfigurationLoader {

    private final Path configurationFile;

    public ConfigurationLoader(Path dataDirectory) {
        this.configurationFile = dataDirectory.resolve("config.yml");
    }

    public JoinMessageConfiguration load() throws ConfigurationException {
        try {
            return JoinMessageConfigurationParser.parse(BukkitYaml.load(configurationFile));
        } catch (IllegalStateException exception) {
            throw new ConfigurationException("Le YAML ne peut pas être lu : " + exception.getMessage(), exception);
        }
    }
}
