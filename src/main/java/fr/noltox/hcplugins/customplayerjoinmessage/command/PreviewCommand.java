package fr.noltox.hcplugins.customplayerjoinmessage.command;

import fr.noltox.hcplugins.customplayerjoinmessage.HCJoinMessage;
import fr.noltox.hcplugins.core.api.HCPluginsCore;
import fr.noltox.hcplugins.customplayerjoinmessage.config.JoinMessageConfiguration;
import fr.noltox.hcplugins.customplayerjoinmessage.message.MessageRenderer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Displays configured messages without changing player data.
 */
public final class PreviewCommand {

    private static final String PREVIEW_DEFAULT_LITERAL = "default";
    private static final Component USAGE = Component.text(
            "Utilisation : /hcplugins joinmessage preview <cosmétique> [--player <joueur>] ou /hcplugins joinmessage preview default --player <joueur>",
            NamedTextColor.RED
    );

    private final HCJoinMessage plugin;
    private final MessageRenderer messageRenderer;

    public PreviewCommand(HCJoinMessage plugin, MessageRenderer messageRenderer) {
        this.plugin = plugin;
        this.messageRenderer = messageRenderer;
    }

    private void sendUsage(CommandSourceStack source) {
        if (isAuthorized(source)) {
            source.getSender().sendMessage(USAGE);
        }
    }

    private boolean isAuthorized(CommandSourceStack source) {
        if (source.getSender().isOp()) {
            return true;
        }
        source.getSender().sendMessage(HCPluginsCore.translations(plugin).operatorOnly());
        return false;
    }

    private static Component createPreview(
            String label,
            Player target,
            Component join,
            Component leave
    ) {
        Component title = Component.text("Aperçu des messages ")
                .decorate(TextDecoration.UNDERLINED)
                .append(Component.text(label).decorate(TextDecoration.UNDERLINED, TextDecoration.BOLD))
                .append(Component.text(" pour " + target.getName() + " :").decorate(TextDecoration.UNDERLINED));

        return Component.empty()
                .append(Component.newline())
                .append(title)
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("Connexion : ", NamedTextColor.GRAY))
                .append(join)
                .append(Component.newline())
                .append(Component.text("Déconnexion : ", NamedTextColor.GRAY))
                .append(leave)
                .append(Component.newline());
    }

    public void execute(CommandSourceStack source, String[] args) {
        if (args.length == 3 && "--player".equals(args[1]) && !args[2].isBlank()) {
            if (PREVIEW_DEFAULT_LITERAL.equalsIgnoreCase(args[0])) {
                previewDefault(source, args[2]);
            } else {
                previewCosmetic(source, args[0], args[2]);
            }
        } else if (args.length == 1 && !PREVIEW_DEFAULT_LITERAL.equalsIgnoreCase(args[0])) {
            previewCosmetic(source, args[0], null);
        } else {
            sendUsage(source);
        }
    }

    public Collection<String> suggest(String[] args) {
        if (args.length <= 1) {
            String prefix = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
            List<String> keys = plugin.activeConfiguration()
                    .map(configuration -> configuration.cosmetics().stream()
                            .map(JoinMessageConfiguration.Cosmetic::id)
                            .filter(id -> !id.equalsIgnoreCase(PREVIEW_DEFAULT_LITERAL))
                            .toList())
                    .orElseGet(List::of);
            return java.util.stream.Stream.concat(java.util.stream.Stream.of(PREVIEW_DEFAULT_LITERAL), keys.stream())
                    .filter(value -> value.toLowerCase(Locale.ROOT).startsWith(prefix)).toList();
        }
        if (args.length == 2) {
            return "--player".startsWith(args[1]) ? List.of("--player") : List.of();
        }
        if (args.length == 3 && "--player".equals(args[1])) {
            return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(args[2].toLowerCase(Locale.ROOT)))
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .toList();
        }
        return List.of();
    }

    private void previewDefault(CommandSourceStack source, String targetName) {
        if (!isAuthorized(source)) {
            return;
        }
        Player target = resolveTarget(source, targetName);
        if (target == null) {
            return;
        }

        JoinMessageConfiguration configuration = plugin.activeConfiguration().orElse(null);
        if (configuration == null) {
            source.getSender().sendMessage(Component.text("Aucune configuration valide n'est actuellement chargée."));
            return;
        }

        renderAndSendPreview(
                source,
                PREVIEW_DEFAULT_LITERAL,
                target,
                configuration.defaults().join(),
                configuration.defaults().leave()
        );
    }

    private void previewCosmetic(CommandSourceStack source, String cosmeticId, String targetName) {
        if (!isAuthorized(source)) {
            return;
        }
        Player target = resolveTarget(source, targetName);
        if (target == null) {
            return;
        }

        JoinMessageConfiguration configuration = plugin.activeConfiguration().orElse(null);
        if (configuration == null) {
            source.getSender().sendMessage(Component.text("Aucune configuration valide n'est actuellement chargée."));
            return;
        }

        JoinMessageConfiguration.Cosmetic cosmetic = configuration.cosmetics().stream()
                .filter(candidate -> candidate.id().equals(cosmeticId))
                .findFirst()
                .orElse(null);
        if (cosmetic == null) {
            source.getSender().sendMessage(Component.text(
                    "La clé cosmétique '" + cosmeticId + "' n'existe pas dans la configuration active."
            ));
            return;
        }
        String join = cosmetic.join().orElse(configuration.defaults().join());
        String leave = cosmetic.leave().orElse(configuration.defaults().leave());

        renderAndSendPreview(source, cosmetic.id(), target, join, leave);
    }

    private void renderAndSendPreview(
            CommandSourceStack source,
            String label,
            Player target,
            String joinTemplate,
            String leaveTemplate
    ) {
        var renderedJoin = messageRenderer.render(joinTemplate, target);
        var renderedLeave = messageRenderer.render(leaveTemplate, target);
        if (renderedJoin.isEmpty() || renderedLeave.isEmpty()) {
            source.getSender().sendMessage(Component.text(
                    "Impossible de résoudre les placeholders ; consultez la console."
            ));
            return;
        }

        source.getSender().sendMessage(createPreview(
                label,
                target,
                renderedJoin.orElseThrow(),
                renderedLeave.orElseThrow()
        ));
    }

    private Player resolveTarget(CommandSourceStack source, String targetName) {
        if (targetName == null) {
            if (source.getSender() instanceof Player player) {
                return player;
            }
            source.getSender().sendMessage(Component.text(
                    "Depuis la console, utilisez /hcplugins joinmessage preview <cosmétique> --player <joueur>."
            ));
            return null;
        }

        Player target = plugin.getServer().getPlayerExact(targetName);
        if (target == null) {
            source.getSender().sendMessage(Component.text("Le joueur '" + targetName + "' n'est pas en ligne."));
        }
        return target;
    }
}
