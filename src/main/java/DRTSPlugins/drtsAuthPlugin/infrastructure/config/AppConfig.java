package DRTSPlugins.drtsAuthPlugin.infrastructure.config;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class AppConfig {
    private final String CONFIG_FILE = "drts-config.yml";

    private final Map<String, String> mainDbConfig;
    private final Map<String, String> cacheConfig;

    public AppConfig() {
        Map<String, Object> config = loadOrCreateConfig();

        this.mainDbConfig = (Map<String, String>) config.get("database");
        this.cacheConfig = (Map<String, String>) config.get("cache");
    }

    private Map<String, Object> loadOrCreateConfig() {
        Path configPath = Paths.get(CONFIG_FILE);
        Yaml yaml = new Yaml();

        if (Files.exists(configPath)) {
            try (InputStream inputStream = Files.newInputStream(configPath)) {
                return yaml.load(inputStream);
            } catch (IOException e) {
                throw new RuntimeException("[DRTS Plugin] Failed to read config file: " + e.getMessage());
            }
        } else {
            Map<String, Object> defaultConfig = new HashMap<>();

            Map<String, String> databaseConfig = new HashMap<>();
            databaseConfig.put("dbms", "postgresql");
            databaseConfig.put("user", "defaultUser");
            databaseConfig.put("password", "defaultPassword");
            databaseConfig.put("database", "defaultDatabase");
            databaseConfig.put("port", "5432");
            databaseConfig.put("host", "127.0.0.1");

            Map<String, String> cacheConfig = new HashMap<>();
            databaseConfig.put("dbms", "redis");
            cacheConfig.put("host", "127.0.0.1");
            cacheConfig.put("port", "6380");

            defaultConfig.put("database", databaseConfig);
            defaultConfig.put("cache", cacheConfig);

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

    public Map<String, String> getMainDbConfig() {
        return mainDbConfig;
    }

    public Map<String, String> getCacheConfig() {
        return cacheConfig;
    }
}
