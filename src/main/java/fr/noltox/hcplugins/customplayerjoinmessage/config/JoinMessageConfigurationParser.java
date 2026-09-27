package fr.noltox.hcplugins.customplayerjoinmessage.config;

import fr.noltox.hcplugins.core.api.message.MiniMessages;
import fr.noltox.hcplugins.customplayerjoinmessage.permission.Permissions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Validates and converts YAML data into the immutable runtime configuration.
 */
public final class JoinMessageConfigurationParser {

    private static final Pattern COSMETIC_ID = Pattern.compile("[a-z0-9][a-z0-9._-]*");

    private JoinMessageConfigurationParser() {
    }

    public static JoinMessageConfiguration parse(YamlConfiguration yaml) throws ConfigurationException {
        ConfigurationSection defaults = yaml.getConfigurationSection("default-messages");
        if (defaults == null) {
            throw new ConfigurationException("La section 'default-messages' est obligatoire.");
        }
        String defaultJoin = requiredMessage(defaults, "join", "default-messages.join");
        String defaultLeave = requiredMessage(defaults, "leave", "default-messages.leave");

        return new JoinMessageConfiguration(
                new JoinMessageConfiguration.DefaultMessages(defaultJoin, defaultLeave),
                parseCosmetics(yaml)
        );
    }

    private static List<JoinMessageConfiguration.Cosmetic> parseCosmetics(YamlConfiguration yaml) throws ConfigurationException {
        if (!yaml.contains("custom-join-messages")) {
            return List.of();
        }

        ConfigurationSection cosmeticsSection = yaml.getConfigurationSection("custom-join-messages");
        if (cosmeticsSection == null) {
            throw new ConfigurationException("La clé 'custom-join-messages' doit être une section YAML.");
        }
        List<JoinMessageConfiguration.Cosmetic> cosmetics = new ArrayList<>(cosmeticsSection.getKeys(false).size());
        for (String id : cosmeticsSection.getKeys(false)) {
            String path = "custom-join-messages." + id;
            ConfigurationSection cosmetic = cosmeticsSection.getConfigurationSection(id);
            if (cosmetic == null) {
                throw new ConfigurationException("La clé '" + path + "' doit être une section YAML.");
            }
            validateCosmeticId(id, path);
            cosmetics.add(new JoinMessageConfiguration.Cosmetic(
                    id,
                    optionalMessage(cosmetic, "join", path + ".join"),
                    optionalMessage(cosmetic, "leave", path + ".leave")
            ));
        }
        return cosmetics;
    }

    private static void validateCosmeticId(String id, String path) throws ConfigurationException {
        if (!COSMETIC_ID.matcher(id).matches()) {
            throw new ConfigurationException("L'identifiant '" + path + "' doit contenir uniquement des minuscules, chiffres, points, tirets ou underscores ; "
                    + "sa permission serait '" + Permissions.cosmetic(id) + "'.");
        }
    }

    private static String requiredMessage(ConfigurationSection section, String key, String path) throws ConfigurationException {
        return optionalMessage(section, key, path)
                .orElseThrow(() -> new ConfigurationException("La clé '" + path + "' est obligatoire."));
    }

    private static Optional<String> optionalMessage(ConfigurationSection section, String key, String path) throws ConfigurationException {
        if (!section.contains(key)) {
            return Optional.empty();
        }
        Object value = section.get(key);
        if (!(value instanceof String message)) {
            throw new ConfigurationException("La clé '" + path + "' doit être une chaîne MiniMessage.");
        }
        try {
            MiniMessages.parseStrict(message);
        } catch (RuntimeException exception) {
            throw new ConfigurationException("Le MiniMessage de la clé '" + path + "' est invalide.", exception);
        }
        return Optional.of(message);
    }
}
