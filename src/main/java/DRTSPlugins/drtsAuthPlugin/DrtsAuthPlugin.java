package DRTSPlugins.drtsAuthPlugin;

import DRTSPlugins.drtsAuthPlugin.commands.LoginCommandExecutor;
import DRTSPlugins.drtsAuthPlugin.commands.RegisterCommandExecutor;
import DRTSPlugins.drtsAuthPlugin.infrastructure.database.PostgreSQLAdapter;
import DRTSPlugins.drtsAuthPlugin.infrastructure.database.repositories.SessionsRepository;
import DRTSPlugins.drtsAuthPlugin.infrastructure.database.repositories.UsersRepository;
import DRTSPlugins.drtsAuthPlugin.listeners.AuthListener;
import DRTSPlugins.drtsAuthPlugin.listeners.PlayerJoinedListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

public final class DrtsAuthPlugin extends JavaPlugin {
    private static final String CONFIG_FILE = "drts-config.yml";

    @Override
    public void onEnable() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch(ClassNotFoundException error) {
            Bukkit.getLogger().info("[DRTS Plugin] Failed to load driver: " + error.getMessage());
        }

        Map<String, String> config = loadOrCreateConfig();

        String user = config.get("user");
        String password = config.get("password");
        String database = config.get("database");
        String port = config.get("port");
        String url = "jdbc:postgresql://127.0.0.1:" + port + "/" + database;

        try {
            PostgreSQLAdapter.connect(url, user, password);

            Bukkit.getLogger().info("[DRTS Plugin] Connected to DB");

            UsersRepository usersRepository = new UsersRepository(PostgreSQLAdapter.getConnection());
            SessionsRepository sessionsRepository = new SessionsRepository();

            PluginManager pluginManager = getServer().getPluginManager();

            pluginManager.registerEvents(new PlayerJoinedListener(this, usersRepository, sessionsRepository), this);
            pluginManager.registerEvents(new AuthListener(sessionsRepository), this);

            getCommand("login").setExecutor(new LoginCommandExecutor(usersRepository, sessionsRepository));
            getCommand("register").setExecutor(new RegisterCommandExecutor(usersRepository, sessionsRepository));
        } catch (SQLException error) {
            Bukkit.getLogger().info("[DRTS Plugin] Failed to connect DB: " + error.getMessage());
        }
    }

    @Override
    public void onDisable() {
        try {
            PostgreSQLAdapter.disconnect();
            Bukkit.getLogger().info("[DRTS Plugin] Disconnected from DB");
        } catch (SQLException error) {
            Bukkit.getLogger().info("[DRTS Plugin] Failed to disconnect DB: " + error.getMessage());
        }
    }

    private static Map<String, String> loadOrCreateConfig() {
        Path configPath = Paths.get(CONFIG_FILE);
        Yaml yaml = new Yaml();

        if (Files.exists(configPath)) {
            try (InputStream inputStream = Files.newInputStream(configPath)) {
                return yaml.load(inputStream);
            } catch (IOException e) {
                throw new RuntimeException("[DRTS Plugin] Failed to read config file: " + e.getMessage());
            }
        } else {
            Map<String, String> defaultConfig = new HashMap<>();
            defaultConfig.put("user", "defaultUser");
            defaultConfig.put("password", "defaultPassword");
            defaultConfig.put("database", "defaultDatabase");
            defaultConfig.put("port", "5432");

            try (Writer writer = Files.newBufferedWriter(configPath)) {
                DumperOptions options = new DumperOptions();
                options.setPrettyFlow(true);
                options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                Yaml dumper = new Yaml(options);
                dumper.dump(defaultConfig, writer);
            } catch (IOException e) {
                throw new RuntimeException("[DRTS Plugin] Failed to create config file: " + e.getMessage());
            }

            return defaultConfig;
        }
    }
}
