package bet.astral.starify.database;

import bet.astral.starify.Starify;
import bet.astral.starify.cache.Status;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Date;
import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class VerifiedDatabase {
    private Connection connection;
    private final Starify starify;

    public VerifiedDatabase(Starify starify) {
        this.starify = starify;
    }

    public CompletableFuture<Void> verify(Player player) {
        return isVerified(player).thenAccept(verified->{
            if (!verified){
                try {
                    PreparedStatement statement = connection.prepareStatement("INSERT INTO users (uniqueId, verified, timestamp) VALUES (?, ?, ?)");
                    statement.setString(1, player.getUniqueId().toString());
                    statement.setInt(2, 1);
                    statement.setLong(3, Instant.now().toEpochMilli());

                    statement.executeUpdate();
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }).exceptionally(throwable->{
            starify.getSLF4JLogger().error("Caught error while trying to verify a player!", throwable);
            return null;
        });
    }
    public CompletableFuture<Status> getStatus(Player player){
        return CompletableFuture.supplyAsync(()->{
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE uniqueId = ?;");
                statement.setString(1, player.getUniqueId().toString());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet != null && resultSet.next()){
                    long timestamp = resultSet.getLong("timestamp");
                    Date date = new Date(timestamp);
                    Instant instant = date.toInstant();

                    Status status = new Status(player.getUniqueId(),
                            resultSet.getInt("verified") == 1,
                            instant);
                    resultSet.close();
                    statement.close();
                    return status;
                }
                statement.close();
                return new Status(player.getUniqueId(), false, Instant.now());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(throwable->{
            starify.getSLF4JLogger().error("Caught error while trying to verify a player!", throwable);
            return null;
        });
    }

    public CompletableFuture<Boolean> isVerified(Player player){
        return getStatus(player).thenApplyAsync(Status::isVerified);
    }

    public CompletableFuture<Void> connect(){
        return CompletableFuture.runAsync(()->{
            try {
                Class.forName("org.sqlite.JDBC");
                File file = new File(starify.getDataFolder(), "verified.db");
                if (!file.exists()){
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }

                connection = DriverManager.getConnection("jdbc:sqlite:"+new File(starify.getDataFolder(), "verified.db"));
            } catch (SQLException | IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(throwable->{
            starify.getSLF4JLogger().error("Caught error while trying to verify a player!", throwable);
            return null;
        });
    }

    public CompletableFuture<Void> createTable(){
        return CompletableFuture.runAsync(()->{
            try {
                PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS users(uniqueId VARCHAR(36), verified INT, timestamp BIGINT)");
                statement.execute();
                statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(throwable->{
            starify.getSLF4JLogger().error("Caught error while trying to verify a player!", throwable);
            return null;
        });
    }
}
