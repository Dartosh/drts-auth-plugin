package DRTSPlugins.drtsAuthPlugin.services;

import DRTSPlugins.drtsAuthPlugin.infrastructure.cache.CacheManager;

import java.util.UUID;

public class SessionsService {
    private static final String SESSION_KEY_PREFIX = "player:session:";

    public void authenticatePlayer(UUID uuid) {
        String key = SESSION_KEY_PREFIX + uuid.toString();

        CacheManager.set(key, "true");
    }

    public void deauthenticatePlayer(UUID uuid) {
        String key = SESSION_KEY_PREFIX + uuid.toString();

        CacheManager.delete(key);
    }

    public boolean isAuthenticated(UUID uuid) {
        String key = SESSION_KEY_PREFIX + uuid.toString();

        String value = CacheManager.get(key);

        return "true".equals(value);
    }
}
