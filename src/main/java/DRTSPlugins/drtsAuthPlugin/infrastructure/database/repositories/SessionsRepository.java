package DRTSPlugins.drtsAuthPlugin.infrastructure.database.repositories;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SessionsRepository {
    private final Set<UUID> authenticatedPlayers = new HashSet<>();

    public void authenticatePlayer(UUID uuid) {
        authenticatedPlayers.add(uuid);
    }

    public void deauthenticatePlayer(UUID uuid) {
        authenticatedPlayers.remove(uuid);
    }

    public boolean isAuthenticated(UUID uuid) {
        return authenticatedPlayers.contains(uuid);
    }
}
