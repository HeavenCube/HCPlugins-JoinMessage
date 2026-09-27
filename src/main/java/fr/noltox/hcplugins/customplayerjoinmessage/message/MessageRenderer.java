package fr.noltox.hcplugins.customplayerjoinmessage.message;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import fr.noltox.hcplugins.core.api.message.MiniMessages;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Resolves PlaceholderAPI values, then parses the resulting MiniMessage once.
 */
public final class MessageRenderer {

    private final JavaPlugin plugin;
    private final Logger logger;
    private boolean failureLogged;

    public MessageRenderer(JavaPlugin plugin, Logger logger) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    public Optional<Component> render(String template, Player player) {
        if (!plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            if (!failureLogged) {
                logger.severe("PlaceholderAPI a été désactivé : les messages personnalisés sont suspendus.");
                failureLogged = true;
            }
            return Optional.empty();
        }

        try {
            String resolved = PlaceholderAPI.setPlaceholders(player, template);
            return Optional.of(MiniMessages.parse(resolved));
        } catch (RuntimeException exception) {
            if (!failureLogged) {
                logger.log(Level.SEVERE, exception, () -> "Impossible de résoudre les placeholders pour " + player.getName()
                        + " : le rendu est ignoré. Cette erreur ne sera plus répétée.");
                failureLogged = true;
            }
            return Optional.empty();
        }
    }
}
