package fr.noltox.hcplugins.customplayerjoinmessage.message;

import fr.noltox.hcplugins.customplayerjoinmessage.HCJoinMessage;
import fr.noltox.hcplugins.customplayerjoinmessage.config.JoinMessageConfiguration;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Resolves and renders the configured message for one player action.
 */
public final class JoinMessageService {

    private final HCJoinMessage plugin;
    private final MessageRenderer messageRenderer;
    private final Logger logger;

    public JoinMessageService(
            HCJoinMessage plugin,
            MessageRenderer messageRenderer,
            Logger logger
    ) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.messageRenderer = Objects.requireNonNull(messageRenderer, "messageRenderer");
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    public Optional<Component> joinMessage(Player player) {
        CosmeticResolver.ResolvedMessages messages = messagesFor(player);
        return messages == null ? Optional.empty() : messageRenderer.render(messages.join(), player);
    }

    public Optional<Component> leaveMessage(Player player) {
        CosmeticResolver.ResolvedMessages messages = messagesFor(player);
        return messages == null ? Optional.empty() : messageRenderer.render(messages.leave(), player);
    }

    /**
     * Broadcasts an already rendered public message to players and to the server console.
     */
    public void broadcast(Component message) {
        plugin.getServer().broadcast(message);
        sendToConsole(message);
    }

    public void sendToConsole(Component message) {
        plugin.getServer().getConsoleSender().sendMessage(message);
    }

    private CosmeticResolver.ResolvedMessages messagesFor(Player player) {
        JoinMessageConfiguration configuration = plugin.activeConfiguration().orElse(null);
        if (configuration == null) {
            return null;
        }

        CosmeticResolver.ResolvedMessages messages = CosmeticResolver.resolve(configuration, player::hasPermission);
        if (messages.hasMultipleMatches()) {
            String selected = messages.cosmetic().orElseThrow().id();
            logger.warning(() -> "Le joueur " + player.getName() + " possède " + messages.matchingCosmetics()
                    + " permissions de messages cosmétiques ; l'entrée YAML '" + selected + "' est utilisée.");
        }
        return messages;
    }
}
