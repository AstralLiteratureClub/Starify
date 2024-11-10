package bet.astral.starify.gui;

import bet.astral.guiman.background.Background;
import bet.astral.guiman.clickable.Clickable;
import bet.astral.guiman.clickable.ClickableBuilder;
import bet.astral.guiman.clickable.ClickableLike;
import bet.astral.guiman.gui.InventoryGUI;
import bet.astral.guiman.utils.ChestRows;
import bet.astral.starify.Starify;
import bet.astral.starify.messenger.Translations;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

public class RequestVerifyGUI {
    private final Map<UUID, Boolean> hasVerified = new HashMap<>();
    private final Map<UUID, Integer> VERIFY_CODE = new HashMap<>();
    private final Random RANDOM = new Random();
    private final InventoryGUI GUI;

    public RequestVerifyGUI(Starify starify) {
        Consumer<Player> CLOSE_CONSUMER = (player) -> {
            if (!hasVerified.get(player.getUniqueId())) {
                hasVerified.remove(player.getUniqueId());
                return;
            }
            openVerify(player);
        };
        Consumer<Player> OPEN_CONSUMER = (player) -> {
            if (hasVerified.get(player.getUniqueId())) {
                hasVerified.put(player.getUniqueId(), true);
                player.getInventory().close();
                return;
            }
            hasVerified.put(player.getUniqueId(), false);
        };
        ClickableBuilder BUTTON_CORRECT = Clickable
                .builder(Material.GREEN_WOOL)
                .title(Translations.GUI_BUTTON_CORRECT_TITLE)
                .actionGeneral(action -> {
                    action.getMessengerInfo().message(action.getWho(), Translations.MESSAGE_CORRECT);
                    hasVerified.put(action.getWho().getUniqueId(), true);
                    action.getWho().getScheduler().run(starify, (task)->action.getWho().closeInventory(), null);
                    starify.getVerifiedDatabase().verify(action.getWho());
                })
                .priority(1)
                ;
        Background BACKGROUND = Background.tooltip(Clickable.builder(Material.RED_WOOL).title(Translations.GUI_BUTTON_WRONG_TITLE)
                .actionGeneral(action -> action.getMessengerInfo().message(action.getWho(), Translations.MESSAGE_WRONG)));
        GUI = InventoryGUI
                .builder(ChestRows.ONE)
                .title(Translations.GUI_TITLE)
                .background(BACKGROUND)
                .clickable(0, clickable(0, BUTTON_CORRECT))
                .clickable(1, clickable(1, BUTTON_CORRECT))
                .clickable(2, clickable(2, BUTTON_CORRECT))
                .clickable(3, clickable(3, BUTTON_CORRECT))
                .clickable(4, clickable(4, BUTTON_CORRECT))
                .clickable(5, clickable(5, BUTTON_CORRECT))
                .clickable(6, clickable(6, BUTTON_CORRECT))
                .clickable(7, clickable(7, BUTTON_CORRECT))
                .clickable(8, clickable(8, BUTTON_CORRECT))
                .openConsumer(OPEN_CONSUMER)
                .closeConsumer(CLOSE_CONSUMER)
                .messenger(starify.getMessenger())
                .build()
        ;
    }

    private @NotNull ClickableLike clickable(int slot, @NotNull ClickableBuilder builder){
        return builder.clone().permission((player)->VERIFY_CODE.get(player.getUniqueId()) == slot);
    }

    public void openVerify(@NotNull Player player) {
        try {
            VERIFY_CODE.put(player.getUniqueId(), RANDOM.nextInt(0, 8));
        } catch (Exception e){
            player.sendMessage(e.getMessage());
        }
        try {
            GUI.open(player);
        } catch (Exception e){
            player.sendMessage(e.getMessage());
        }
    }
}