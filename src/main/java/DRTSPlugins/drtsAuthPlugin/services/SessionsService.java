package DRTSPlugins.drtsAuthPlugin.services;

import DRTSPlugins.drtsAuthPlugin.infrastructure.cache.CacheManager;

import java.util.UUID;

public class SessionsService {
    private static final String SESSION_KEY_PREFIX = "player:session:";
    private static final String SESSION_KEY_SEPARATOR = ":";

    public void authenticatePlayer(UUID uuid, String ip) {
        String key = SESSION_KEY_PREFIX + uuid.toString() + SESSION_KEY_SEPARATOR + ip;

        CacheManager.set(key, "true");
    }

    public void deauthenticatePlayer(UUID uuid, String ip) {
        String key = SESSION_KEY_PREFIX + uuid.toString() + SESSION_KEY_SEPARATOR + ip;

        CacheManager.delete(key);
    }

    public boolean isAuthenticated(UUID uuid, String ip) {
        String key = SESSION_KEY_PREFIX + uuid.toString() + SESSION_KEY_SEPARATOR + ip;

        String value = CacheManager.get(key);

        return "true".equals(value);
    }
}
