package fr.noltox.hcplugins.customplayerjoinmessage.listener;

import de.myzelyam.api.vanish.*;
import fr.noltox.hcplugins.customplayerjoinmessage.message.JoinMessageService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Replaces PremiumVanish fake join and quit announcements with configured messages.
 */
public final class PremiumVanishListener implements Listener {

    private final JoinMessageService messageService;
    private final Map<UUID, Announcement> pendingAnnouncements = new HashMap<>();

    public PremiumVanishListener(JoinMessageService messageService) {
        this.messageService = messageService;
    }

    public boolean isVanished(Player player) {
        return VanishAPI.isInvisible(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHide(PlayerHideEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        pendingAnnouncements.remove(playerId);
        if (!event.isSilent()) {
            event.setSilent(true);
            pendingAnnouncements.put(playerId, Announcement.LEAVE);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onShow(PlayerShowEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        pendingAnnouncements.remove(playerId);
        if (!event.isSilent()) {
            event.setSilent(true);
            pendingAnnouncements.put(playerId, Announcement.JOIN);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCancelledHide(PlayerHideEvent event) {
        if (event.isCancelled()) {
            pendingAnnouncements.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCancelledShow(PlayerShowEvent event) {
        if (event.isCancelled()) {
            pendingAnnouncements.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        pendingAnnouncements.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHidden(PostPlayerHideEvent event) {
        if (pendingAnnouncements.remove(event.getPlayer().getUniqueId(), Announcement.LEAVE)) {
            messageService.leaveMessage(event.getPlayer()).ifPresent(messageService::broadcast);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onShown(PostPlayerShowEvent event) {
        if (pendingAnnouncements.remove(event.getPlayer().getUniqueId(), Announcement.JOIN)) {
            messageService.joinMessage(event.getPlayer()).ifPresent(messageService::broadcast);
        }
    }

    private enum Announcement {
        JOIN,
        LEAVE
    }
}
