package DRTSPlugins.drtsAuthPlugin.infrastructure.database.repositories;

import DRTSPlugins.drtsAuthPlugin.infrastructure.database.entities.UserEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UsersRepository {
    private final Connection connection;

    public UsersRepository(Connection connection) {
        this.connection = connection;
    }

    public void saveUser(String username, String password) throws SQLException {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            preparedStatement.setString(2, password);

            preparedStatement.executeUpdate();
        }
    }

    public Optional<UserEntity> getUserByUsername(String username) throws SQLException {
        String query = "SELECT id, username, password FROM users WHERE username = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String fetchedUsername = resultSet.getString("username");
                    String password = resultSet.getString("password");

                    UserEntity user = new UserEntity(id, fetchedUsername, password);

                    return Optional.of(user);
                }
            }
        }

        return Optional.empty();
    }
}
