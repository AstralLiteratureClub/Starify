package bet.astral.starify.messenger;

import bet.astral.messenger.v2.component.ComponentType;
import bet.astral.messenger.v2.translation.Translation;

import static bet.astral.messenger.v2.translation.Translation.text;

public class Translations {
    public static final Translation GUI_TITLE = new Translation("gui.title").add(ComponentType.CHAT, text("Click the <green>GREEN <reset>wool"));
    public static final Translation GUI_BUTTON_WRONG_TITLE = new Translation("gui.button.wrong").add(ComponentType.CHAT, text("<!italic><red>Wrong"));
    public static final Translation GUI_BUTTON_CORRECT_TITLE = new Translation("gui.button.correct").add(ComponentType.CHAT, text("<!italic><green>Correct"));
    public static final Translation MESSAGE_WRONG = new Translation("message.wrong").add(ComponentType.ACTION_BAR, text("<red>Incorrect Button"));
    public static final Translation MESSAGE_CORRECT = new Translation("message.correct").add(ComponentType.ACTION_BAR, text("<green>Verified!"));

}
