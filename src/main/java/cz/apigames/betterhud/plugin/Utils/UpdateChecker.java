package cz.apigames.betterhud.plugin.utils;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.api.utils.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker implements Listener {

    private static final String API = "https://api.spigotmc.org/legacy/update.php?resource=";
    private static final String ID = "84180";

    private static boolean newUpdate;
    private static String latestVersion;

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("betterhud.update.notify")
                && newUpdate) {

            event.getPlayer().sendMessage(MessageUtils.colorize(" &eBetterHud &8» &7New version available! Your version: &e" + BetterHud.getVersion() + " &7| Latest version: &6" + latestVersion));
            event.getPlayer().sendMessage(MessageUtils.colorize(" &eDownload here &8» &6https://spigotmc.org/resource/84180"));

        }
    }

    public static void checkUpdate() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(API + ID).openConnection();
            connection.setRequestMethod("GET");
            latestVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();

            if (BetterHud.getVersion().contains("SNAPSHOT")) {
                newUpdate = false;
                BetterHud.sendMessageToConsole("You are using a SNAPSHOT version, which means, that some functions can be unstable. &cUse this version on production server at your own risk.");
                return;
            }

            if (!BetterHud.getVersion().equalsIgnoreCase(latestVersion)) {
                newUpdate = true;
                return;
            }

        } catch (IOException e) {
            BetterHud.error("Failed to check for the latest version!", e);
        }
        newUpdate = false;
    }

}
