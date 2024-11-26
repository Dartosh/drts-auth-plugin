package DRTSPlugins.drtsAuthPlugin.listeners;

import DRTSPlugins.drtsAuthPlugin.services.SessionsService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

import java.net.InetSocketAddress;

public class AuthListener implements Listener {
    private final SessionsService sessionsService;

    public AuthListener(SessionsService sessionsService) {
        this.sessionsService = sessionsService;
    }

    private void handleEvent(Player player, org.bukkit.event.Cancellable event) {
        InetSocketAddress socketAddress = player.getAddress();

        if (!sessionsService.isAuthenticated(player.getUniqueId(), socketAddress.getAddress().getHostAddress())) {
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
    public void onBlockBreak(BlockBreakEvent event) {
        handleEvent(event.getPlayer(), event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        handleEvent(event.getPlayer(), event);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        handleEvent(event.getPlayer(), event);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        handleEvent(event.getPlayer(), event);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        handleEvent(event.getPlayer(), event);
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            handleEvent(player, event);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player player) {
            handleEvent(player, event);
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        handleEvent(event.getPlayer(), event);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        handleEvent(event.getPlayer(), event);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            handleEvent(player, event);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            InetSocketAddress socketAddress = player.getAddress();
            if (!sessionsService.isAuthenticated(player.getUniqueId(), socketAddress.getAddress().getHostAddress())) {
                event.setCancelled(true);
            }
        }
    }



//    @EventHandler
//    public void onPlayerQuit(PlayerQuitEvent event) {
//        Player player = event.getPlayer();
//
//        InetSocketAddress socketAddress = player.getAddress();
//
//        sessionsService.deauthenticatePlayer(player.getUniqueId(), socketAddress.getAddress().getHostAddress());
//    }
}
