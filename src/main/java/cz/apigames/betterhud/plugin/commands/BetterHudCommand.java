package cz.apigames.betterhud.plugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.api.BetterHudAPI;
import cz.apigames.betterhud.api.displays.Display;
import cz.apigames.betterhud.api.displays.DisplayType;
import cz.apigames.betterhud.api.utils.ToggleCommand;
import cz.apigames.betterhud.plugin.utils.ConfigManager;
import cz.apigames.betterhud.plugin.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@CommandAlias("betterhud|bh")
public class BetterHudCommand extends BaseCommand {

    @Subcommand("reload")
    @CommandPermission("betterhud.command.reload")
    public void onReload(final CommandSender sender) {
        long time = System.currentTimeMillis();

        //TEMP CACHE
        HashMap<Player, HashMap<DisplayType, String>> cachedActiveHuds = new HashMap<>();

        Display.getDisplays().forEach(display -> {

            HashMap<DisplayType, String> huds;
            if(cachedActiveHuds.containsKey(display.getPlayer())) {
                huds = cachedActiveHuds.get(display.getPlayer());
            } else {
                huds = new HashMap<>();
            }

            huds.put(DisplayType.getDisplayType(display), display.getHud().getName());
            cachedActiveHuds.put(display.getPlayer(), huds);

        });

        BetterHud.getAPI().unload();

        ConfigManager.reloadConfig("config.yml");
        ConfigManager.reloadConfig("messages.yml");
        ConfigManager.reloadConfig("characters.yml");

        //TOGGLE COMMAND MESSAGES
        ToggleCommand.setEnableMessage(ConfigManager.getConfig("messages.yml").getString("messages.toggle-custom-on", ""));
        ToggleCommand.setDisableMessage(ConfigManager.getConfig("messages.yml").getString("messages.toggle-custom-off", ""));

        List<String> errors = BetterHud.getAPI().load(new File(BetterHud.getPlugin().getDataFolder(), "config.yml"), true);
        Future<Boolean> FontImageFiles_success = BetterHud.getAPI().generateFontImageFiles(new File(BetterHud.getPlugin().getDataFolder(), "characters.yml"), new File("plugins/ItemsAdder/data/items_packs/betterhud"));

        try {
            if(FontImageFiles_success.get(5, TimeUnit.SECONDS)) {

                //ASYNC
                Bukkit.getScheduler().runTaskAsynchronously(BetterHud.getPlugin(), () -> {

                    boolean iaReload = false;

                    Set<String> updatedChecksums = new HashSet<>();
                    for(File child : BetterHudAPI.getFontImagesDirectory().listFiles()) {

                        String checksum = FileUtils.checksum(child);
                        updatedChecksums.add(checksum);

                        if(!BetterHud.checksums.contains(checksum)) {
                            iaReload = true;
                        }

                    }
                    BetterHud.checksums.clear();
                    BetterHud.checksums.addAll(updatedChecksums);

                    if(iaReload) {
                        Bukkit.getScheduler().runTask(BetterHud.getPlugin(), () -> {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "iazip");
                            sender.sendMessage(BetterHud.getMessage("reload-itemsadder"));
                        });
                    }

                    //SHOW ACTIVE DISPLAYS
                    cachedActiveHuds.forEach((player, huds) -> huds.forEach((displayType, s) -> {

                        if(BetterHud.getAPI().hudExists(s)) {
                            Display.createDisplay(player, BetterHud.getAPI().getHud(s), displayType);
                        }

                    }));

                    cachedActiveHuds.clear();

                });

                //ERROR MESSAGE
                if(!errors.isEmpty()) {
                    BetterHud.sendErrorToConsole("========================================");
                    BetterHud.sendErrorToConsole("BetterHud - Found configuration errors");
                    BetterHud.sendErrorToConsole(" ");
                    errors.forEach(BetterHud::sendErrorToConsole);
                    BetterHud.sendErrorToConsole(" ");
                    BetterHud.sendErrorToConsole("========================================");
                    sender.sendMessage(BetterHud.getMessage("reload-error"));
                    return;
                }

                sender.sendMessage(BetterHud.getMessage("reload-successful").replace("{time}", String.valueOf(System.currentTimeMillis()-time)));

            }
        } catch (InterruptedException | ExecutionException e) {
            BetterHud.error("An error occurred while waiting for FontImage file generation task completion.", e);
            sender.sendMessage(BetterHud.getMessage("reload-error"));
        } catch (TimeoutException e) {
            BetterHud.error("FontImage files generation took too long! Reload task was terminated to keep thread safe.", e);
            sender.sendMessage(BetterHud.getMessage("reload-error"));
        }
    }
}
