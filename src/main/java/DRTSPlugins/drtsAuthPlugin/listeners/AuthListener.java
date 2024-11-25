package DRTSPlugins.drtsAuthPlugin.listeners;

import DRTSPlugins.drtsAuthPlugin.infrastructure.database.repositories.SessionsRepository;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class AuthListener implements Listener {
    private final SessionsRepository sessionsRepository;

    public AuthListener(SessionsRepository sessionsRepository) {
        this.sessionsRepository = sessionsRepository;
    }

    private void handleEvent(Player player, org.bukkit.event.Cancellable event) {
        if (!sessionsRepository.isAuthenticated(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Вы должны войти или зарегистрироваться, чтобы сделать это.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        handleEvent(event.getPlayer(), event);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        sessionsRepository.deauthenticatePlayer(player.getUniqueId());
    }
}
