package fr.noltox.hcplugins.customplayerjoinmessage.command;

import fr.noltox.hcplugins.core.api.command.CoreCommand;
import fr.noltox.hcplugins.core.api.HCPluginsCore;
import fr.noltox.hcplugins.core.api.message.CoreTranslations;
import fr.noltox.hcplugins.customplayerjoinmessage.HCJoinMessage;
import fr.noltox.hcplugins.customplayerjoinmessage.message.MessageRenderer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/** Commands under /hcplugins joinmessage. */
public final class ReloadCommand implements CoreCommand {

    private final HCJoinMessage plugin;
    private final PreviewCommand preview;
    private final CoreTranslations translations;

    public ReloadCommand(HCJoinMessage plugin, MessageRenderer renderer) {
        this.plugin = plugin;
        this.translations = HCPluginsCore.translations(plugin);
        this.preview = new PreviewCommand(plugin, renderer);
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (args.length == 1 && "reload".equalsIgnoreCase(args[0])) {
            if (!source.getSender().isOp()) {
                source.getSender().sendMessage(translations.operatorOnly());
                return;
            }
            long started = System.nanoTime();
            boolean reloaded = plugin.reloadMessages();
            source.getSender().sendMessage(reloaded
                    ? translations.reloadSuccess(plugin, System.nanoTime() - started)
                    : translations.reloadFailure(plugin));
            return;
        }
        if (args.length > 0 && "preview".equalsIgnoreCase(args[0])) {
            preview.execute(source, java.util.Arrays.copyOfRange(args, 1, args.length));
            return;
        }
        source.getSender().sendMessage(Component.text("Utilisation : /hcplugins joinmessage <reload|preview>"));
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        if (!source.getSender().isOp()) {
            return List.of();
        }
        if (args.length <= 1) {
            String prefix = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
            return List.of("reload", "preview").stream().filter(value -> value.startsWith(prefix)).toList();
        }
        if ("preview".equalsIgnoreCase(args[0])) {
            return preview.suggest(java.util.Arrays.copyOfRange(args, 1, args.length));
        }
        return List.of();
    }
}
