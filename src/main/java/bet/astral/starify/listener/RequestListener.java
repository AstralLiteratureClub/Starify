package bet.astral.starify.listener;

import bet.astral.starify.Starify;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

public class RequestListener implements Listener {
    private final Starify starify;

    public RequestListener(Starify starify) {
        this.starify = starify;
    }


    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        starify.getVerifiedDatabase().isVerified(event.getPlayer())
                        .thenAccept(verified->{
                            if (!verified){
                                starify.getRequestVerify().openVerify(event.getPlayer());
                            }
                        });
    }
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(@NotNull PlayerRespawnEvent event) {
        starify.getVerifiedDatabase().isVerified(event.getPlayer())
                .thenAccept(verified->{
                    if (!verified){
                        starify.getRequestVerify().openVerify(event.getPlayer());
                    }
                });
    }

}
