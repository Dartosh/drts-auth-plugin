package DRTSPlugins.drtsAuthPlugin.commands;

import DRTSPlugins.drtsAuthPlugin.infrastructure.database.entities.UserEntity;
import DRTSPlugins.drtsAuthPlugin.infrastructure.database.repositories.UsersRepository;
import DRTSPlugins.drtsAuthPlugin.services.SessionsService;
import DRTSPlugins.drtsAuthPlugin.utils.PasswordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Optional;

public class LoginCommandExecutor implements CommandExecutor {
    private final UsersRepository usersRepository;
    private final SessionsService sessionsService;

    public LoginCommandExecutor(UsersRepository usersRepository, SessionsService sessionsService) {
        this.usersRepository = usersRepository;
        this.sessionsService = sessionsService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду могут использовать только игроки.");
            return true;
        }

        Player player = (Player) sender;

        if (sessionsService.isAuthenticated(player.getUniqueId())) {
            player.sendMessage(ChatColor.GREEN + "Вы уже вошли в систему.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Использование: /login <пароль>");
            return true;
        }

        String password = args[0];
        String playerName = player.getName();

        try {
            Optional<UserEntity> userOptional = usersRepository.getUserByUsername(playerName);

            if (userOptional.isPresent()) {
                UserEntity user = userOptional.get();
                String storedHash = user.getPassword();

                if (PasswordUtils.verifyPassword(password, storedHash)) {
                    sessionsService.authenticatePlayer(player.getUniqueId());
                    player.sendMessage(ChatColor.GREEN + "Вы успешно вошли в систему!");
                } else {
                    player.sendMessage(ChatColor.RED + "Неверный пароль.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Вы не зарегистрированы. Используйте /register <пароль> <подтверждение пароля> для регистрации.");
            }
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "Произошла ошибка при доступе к базе данных.");
            e.printStackTrace();
        }

        return true;
    }
}
