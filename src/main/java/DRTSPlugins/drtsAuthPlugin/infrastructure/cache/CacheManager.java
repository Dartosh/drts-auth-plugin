package DRTSPlugins.drtsAuthPlugin.infrastructure.cache;

import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class CacheManager {
    private static JedisPool jedisPool;

    public static void connect(Integer port, String host) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();

        poolConfig.setMaxTotal(128);

        jedisPool = new JedisPool(poolConfig, host, port);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.ping();
            Bukkit.getLogger().info("[DRTS Auth Plugin] Successfully connected to the Redis");
        } catch (Exception e) {
            Bukkit.getLogger().severe("[DRTS Auth Plugin] Failed to connect to the Redis: " + e.getMessage());
        }
    }

    public static void disconnect() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    public static void set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[DRTS Auth Plugin] Error setting value in Redis: " + e.getMessage());
        }
    }

    public static String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[DRTS Auth Plugin] Error getting value from Redis: " + e.getMessage());
            return null;
        }
    }

    public static void delete(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[DRTS Auth Plugin] Error deleting key in Redis: " + e.getMessage());
        }
    }
}
