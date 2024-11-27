package DRTSPlugins.drtsAuthPlugin.listeners;

import DRTSPlugins.drtsAuthPlugin.DrtsAuthPlugin;
import DRTSPlugins.drtsAuthPlugin.infrastructure.database.entities.UserEntity;
import DRTSPlugins.drtsAuthPlugin.infrastructure.database.repositories.UsersRepository;
import DRTSPlugins.drtsAuthPlugin.services.SessionsService;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class PlayerJoinedListener implements Listener {
    private final DrtsAuthPlugin plugin;
    private final UsersRepository usersRepository;
    private final SessionsService sessionsService;

    public PlayerJoinedListener(DrtsAuthPlugin plugin, UsersRepository usersRepository, SessionsService sessionsService) {
        this.usersRepository = usersRepository;
        this.sessionsService = sessionsService;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        String playerName = player.getName();
        InetSocketAddress socketAddress = player.getAddress();
        String playerAddress = socketAddress.getAddress().getHostAddress();


        if (sessionsService.isAuthenticated(playerUUID, playerAddress)) {
            player.sendMessage(ChatColor.GREEN + "Добро пожаловать обратно!");
        } else {
            player.setMetadata("preAuthAllowFlight", new FixedMetadataValue(plugin, player.getAllowFlight()));

            player.setAllowFlight(true);

            try {
                Optional<UserEntity> userOptional = usersRepository.getUserByUsername(playerName);

                if (userOptional.isPresent()) {
                    player.sendMessage(ChatColor.YELLOW + "Пожалуйста, войдите с помощью команды /login <пароль>");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Пожалуйста, зарегистрируйтесь с помощью команды /register <пароль> <пароль>");
                }

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (!sessionsService.isAuthenticated(playerUUID, playerAddress) && player.isOnline()) {
                        player.kickPlayer(ChatColor.RED + "Вы слишком долго не входили в систему!");
                    }
                }, 800L);
            } catch (SQLException e) {
                player.sendMessage(ChatColor.RED + "Произошла ошибка при доступе к базе данных.");

                e.printStackTrace();
            }
        }
    }
}
