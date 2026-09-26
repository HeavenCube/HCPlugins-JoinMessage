package fr.noltox.hcplugins.customplayerjoinmessage.listener;

import fr.noltox.hcplugins.customplayerjoinmessage.message.JoinMessageService;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public final class JoinQuitListener implements Listener {

    private final JoinMessageService messageService;
    private final Predicate<Player> isVanished;
    private final Set<PlayerEvent> vanishedAtEventStart = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Map<PlayerQuitEvent, Component> preparedQuitMessages = new IdentityHashMap<>();

    public JoinQuitListener(JoinMessageService messageService, Predicate<Player> isVanished) {
        this.messageService = messageService;
        this.isVanished = isVanished;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoinStart(PlayerJoinEvent event) {
        if (isVanished.test(event.getPlayer())) {
            vanishedAtEventStart.add(event);
            event.joinMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        if (vanishedAtEventStart.remove(event) || isVanished.test(event.getPlayer())) {
            event.joinMessage(null);
            return;
        }
        if (event.joinMessage() == null) {
            return;
        }
        messageService.joinMessage(event.getPlayer()).ifPresent(message -> {
            event.joinMessage(message);
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuitStart(PlayerQuitEvent event) {
        if (isVanished.test(event.getPlayer())) {
            vanishedAtEventStart.add(event);
            event.quitMessage(null);
        }
    }

    /**
     * Resolves placeholders before plugins such as TAB release their player state at HIGHEST.
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void prepareQuit(PlayerQuitEvent event) {
        if (vanishedAtEventStart.contains(event) || isVanished.test(event.getPlayer()) || event.quitMessage() == null) {
            return;
        }
        messageService.leaveMessage(event.getPlayer()).ifPresent(message -> preparedQuitMessages.put(event, message));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        Component message = preparedQuitMessages.remove(event);
        if (vanishedAtEventStart.remove(event) || isVanished.test(event.getPlayer())) {
            event.quitMessage(null);
            return;
        }
        if (event.quitMessage() == null) {
            return;
        }
        if (message != null) {
            event.quitMessage(message);
        }
    }
}
