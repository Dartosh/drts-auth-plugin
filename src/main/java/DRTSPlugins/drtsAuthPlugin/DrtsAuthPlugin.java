package DRTSPlugins.drtsAuthPlugin;

import DRTSPlugins.drtsAuthPlugin.commands.LoginCommandExecutor;
import DRTSPlugins.drtsAuthPlugin.commands.RegisterCommandExecutor;
import DRTSPlugins.drtsAuthPlugin.infrastructure.cache.CacheManager;
import DRTSPlugins.drtsAuthPlugin.infrastructure.config.AppConfig;
import DRTSPlugins.drtsAuthPlugin.infrastructure.database.PostgreSQLAdapter;
import DRTSPlugins.drtsAuthPlugin.infrastructure.database.repositories.UsersRepository;
import DRTSPlugins.drtsAuthPlugin.listeners.AuthListener;
import DRTSPlugins.drtsAuthPlugin.listeners.PlayerJoinedListener;
import DRTSPlugins.drtsAuthPlugin.services.SessionsService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import java.sql.SQLException;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

public final class DrtsAuthPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch(ClassNotFoundException error) {
            Bukkit.getLogger().info("[DRTS Plugin] Failed to load driver: " + error.getMessage());
        }

        AppConfig appConfig = new AppConfig();

        Map<String, String> databaseConfig = appConfig.getMainDbConfig();

        String dbUser = databaseConfig.get("user");
        String dbPassword = databaseConfig.get("password");
        String dbDatabase = databaseConfig.get("database");
        String dbHost = databaseConfig.get("host");
        String dbPort = databaseConfig.get("port");
        String mainDbms = databaseConfig.get("dbms");
        String dbUrl = "jdbc:" + mainDbms + "://" + dbHost + ":" + dbPort + "/" + dbDatabase;

        Map<String, String> cacheConfig = appConfig.getCacheConfig();

        String cacheHost = cacheConfig.get("host");
        int cachePort = Integer.parseInt(cacheConfig.get("port"));

        try {
            CacheManager.connect(cachePort, cacheHost);
            PostgreSQLAdapter.connect(dbUrl, dbUser, dbPassword);

            Bukkit.getLogger().info("[DRTS Auth Plugin] Connected to DB");

            UsersRepository usersRepository = new UsersRepository(PostgreSQLAdapter.getConnection());
            SessionsService sessionsService = new SessionsService();

            PluginManager pluginManager = getServer().getPluginManager();

            pluginManager.registerEvents(new PlayerJoinedListener(this, usersRepository, sessionsService), this);
            pluginManager.registerEvents(new AuthListener(sessionsService), this);

            getCommand("login").setExecutor(new LoginCommandExecutor(usersRepository, sessionsService, this));
            getCommand("register").setExecutor(new RegisterCommandExecutor(usersRepository, sessionsService, this));
        } catch (SQLException error) {
            Bukkit.getLogger().info("[DRTS Auth Plugin] Failed to connect DB: " + error.getMessage());
        }
    }

    @Override
    public void onDisable() {
        try {
            PostgreSQLAdapter.disconnect();
            Bukkit.getLogger().info("[DRTS Auth Plugin] Disconnected from DB");
        } catch (SQLException error) {
            Bukkit.getLogger().info("[DRTS Auth Plugin] Failed to disconnect DB: " + error.getMessage());
        }


        CacheManager.disconnect();
        Bukkit.getLogger().info("[DRTS Auth Plugin] Disconnected from Cache DB");
    }
}
