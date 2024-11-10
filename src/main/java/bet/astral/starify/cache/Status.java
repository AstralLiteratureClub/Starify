package bet.astral.starify.cache;

import java.time.Instant;
import java.util.UUID;

public class Status {
    private final UUID uniqueId;
    private final boolean verified;
    private final Instant timestamp;

    public Status(UUID uniqueId, boolean verified, Instant timestamp) {
        this.uniqueId = uniqueId;
        this.verified = verified;
        this.timestamp = timestamp;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public boolean isVerified() {
        return verified;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
