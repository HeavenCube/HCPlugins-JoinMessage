package fr.noltox.hcplugins.customplayerjoinmessage;

import fr.noltox.hcplugins.core.api.HCPluginsCore;
import fr.noltox.hcplugins.core.api.command.CoreCommandRegistration;
import fr.noltox.hcplugins.core.api.config.HCPluginFiles;
import fr.noltox.hcplugins.customplayerjoinmessage.command.ReloadCommand;
import fr.noltox.hcplugins.customplayerjoinmessage.config.ConfigurationException;
import fr.noltox.hcplugins.customplayerjoinmessage.config.ConfigurationLoader;
import fr.noltox.hcplugins.customplayerjoinmessage.config.JoinMessageConfiguration;
import fr.noltox.hcplugins.customplayerjoinmessage.listener.JoinQuitListener;
import fr.noltox.hcplugins.customplayerjoinmessage.listener.PremiumVanishListener;
import fr.noltox.hcplugins.customplayerjoinmessage.message.JoinMessageService;
import fr.noltox.hcplugins.customplayerjoinmessage.message.MessageRenderer;
import fr.noltox.hcplugins.core.api.permission.DynamicPermissionRegistry;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;

public final class HCJoinMessage extends JavaPlugin {

    private JoinMessageConfiguration activeConfiguration;
    private ConfigurationLoader configurationLoader;
    private DynamicPermissionRegistry cosmeticPermissions;
    private CoreCommandRegistration commandRegistration;

    @Override
    public void onEnable() {
        if (!getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().severe("PlaceholderAPI est obligatoire et doit être activé avant ce plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Path configurationFile = HCPluginFiles.singleConfiguration(this);
        HCPluginFiles.copyDefault(this, "config.yml", configurationFile);
        configurationLoader = new ConfigurationLoader(configurationFile);
        cosmeticPermissions = new DynamicPermissionRegistry(
                getServer().getPluginManager(),
                getLogger(),
                PermissionDefault.FALSE
        );
        MessageRenderer messageRenderer = new MessageRenderer(this, getLogger());
        JoinMessageService messageService = new JoinMessageService(this, messageRenderer, getLogger());

        if (!reloadMessages()) {
            getLogger().severe("La configuration initiale est invalide : les messages Paper par défaut restent actifs jusqu'à un rechargement réussi.");
        }

        Predicate<Player> isVanished = player -> false;
        if (getServer().getPluginManager().isPluginEnabled("PremiumVanish")) {
            PremiumVanishListener premiumVanishListener = new PremiumVanishListener(messageService);
            getServer().getPluginManager().registerEvents(
                    premiumVanishListener,
                    this
            );
            isVanished = premiumVanishListener::isVanished;
            getLogger().info("Intégration PremiumVanish activée.");
        }
        getServer().getPluginManager().registerEvents(new JoinQuitListener(messageService, isVanished), this);

        ReloadCommand commands = new ReloadCommand(this, messageRenderer);
        commandRegistration = HCPluginsCore.require(this).register(
                this,
                "joinmessage",
                "Messages de connexion et de déconnexion",
                java.util.List.of(),
                commands
        );
    }

    public boolean reloadMessages() {
        try {
            JoinMessageConfiguration candidate = configurationLoader.load();
            cosmeticPermissions.synchronize(candidate.cosmetics().stream()
                    .map(JoinMessageConfiguration.Cosmetic::permission)
                    .toList());
            activeConfiguration = candidate;
            getLogger().info(() -> "Configuration des messages cosmétiques rechargée (" + candidate.cosmetics().size() + " cosmétique(s)).");
            return true;
        } catch (ConfigurationException | RuntimeException exception) {
            getLogger().log(Level.SEVERE,
                    "Échec du rechargement de config.yml ; la configuration précédente reste active.",
                    exception);
            return false;
        }
    }

    public Optional<JoinMessageConfiguration> activeConfiguration() {
        return Optional.ofNullable(activeConfiguration);
    }

    @Override
    public void onDisable() {
        try {
            if (commandRegistration != null) {
                commandRegistration.close();
            }
        } finally {
            if (cosmeticPermissions != null) {
                cosmeticPermissions.unregisterAll();
            }
            activeConfiguration = null;
        }
    }
}
