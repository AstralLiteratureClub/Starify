package bet.astral.starify;

import bet.astral.guiman.GUIMan;
import bet.astral.messenger.v2.Messenger;
import bet.astral.messenger.v2.locale.LanguageTable;
import bet.astral.messenger.v2.locale.source.LanguageSource;
import bet.astral.messenger.v2.locale.source.gson.GsonLanguageSource;
import bet.astral.messenger.v2.translation.serializer.gson.TranslationGsonHelper;
import bet.astral.messenger.v3.minecraft.paper.PaperMessenger;
import bet.astral.starify.database.VerifiedDatabase;
import bet.astral.starify.gui.RequestVerifyGUI;
import bet.astral.starify.listener.RequestListener;
import bet.astral.starify.messenger.Translations;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class Starify extends JavaPlugin {
    private RequestVerifyGUI requestVerify;
    private VerifiedDatabase verifiedDatabase;
    private Messenger messenger;

    @Override
    public void onEnable() {
        PaperMessenger.init(this);
        GUIMan.init(this);
        verifiedDatabase = new VerifiedDatabase(this);
        verifiedDatabase.connect().thenRun(()->verifiedDatabase.createTable());

        messenger = new PaperMessenger(getComponentLogger());
        requestVerify = new RequestVerifyGUI(this);

        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        JsonObject messages = TranslationGsonHelper.getDefaults(Translations.class, MiniMessage.miniMessage(), gson);
        File file = new File(getDataFolder(), "en_us.json");
        if (!file.exists()){
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(messages));
            writer.flush();
            writer.close();

            LanguageSource source = new GsonLanguageSource(messenger, Locale.US, file, MiniMessage.miniMessage());
            LanguageTable table = LanguageTable.of(source);
            messenger.setDefaultLocale(source);
            messenger.registerLanguageTable(Locale.US, table);
            messenger.loadTranslations(Translations.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        getServer().getPluginManager().registerEvents(new RequestListener(this), this);
    }

    public RequestVerifyGUI getRequestVerify() {
        return requestVerify;
    }

    public VerifiedDatabase getVerifiedDatabase() {
        return verifiedDatabase;
    }

    public Messenger getMessenger() {
        return messenger;
    }
}
