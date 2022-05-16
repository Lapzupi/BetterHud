package cz.apigames.betterhud.plugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.api.BetterHudAPI;
import cz.apigames.betterhud.api.displays.Display;
import cz.apigames.betterhud.api.displays.DisplayType;
import cz.apigames.betterhud.api.utils.MessageUtils;
import cz.apigames.betterhud.api.utils.ToggleCommand;
import cz.apigames.betterhud.plugin.utils.ConfigManager;
import cz.apigames.betterhud.plugin.utils.FileUtils;
import cz.apigames.betterhud.plugin.utils.JsonMessage;
import cz.apigames.betterhud.plugin.utils.TextureExtractor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
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

    @Subcommand("extractTextures")
    @CommandPermission("betterhud.command.extracttextures")
    public void onExtractTextures(final CommandSender sender) {
        try {
            if(TextureExtractor.extract()) {
                sender.sendMessage(BetterHud.getMessage("extract-textures-success"));
            } else {
                sender.sendMessage(BetterHud.getMessage("extract-textures-error"));
            }
        } catch (IOException e) {
            BetterHud.error("Failed to extract textures from JAR file!", e);
            sender.sendMessage(BetterHud.getMessage("extract-textures-error"));
        }
    }

    @Subcommand("test")
    public void onTest(final Player player) {
        if(!Display.getDisplays(player).isEmpty()) {
            Display.getDisplays(player).forEach(display -> display.getHud().getElements().forEach(element -> {
                player.sendMessage("-------------------------");
                player.sendMessage("ElementName: "+element.getName());
                player.sendMessage("X,Y: "+element.getX() + ";" + element.getY());
                player.sendMessage("iX,iY: "+element.ix + ";" + element.iy);
                player.sendMessage("width: "+element.calculateWidth(player));
            }));
        }
    }


    public void onShow() {

    }

    public void onHide(){

    }


    public void onSetValue() {

    }

    public void onGetValue() {

    }


    public void onResetValue() {

    }

    public void onShowElement() {

    }

    public void onHideElement(){

    }

    public void onSetX() {

    }

    @Default
    @Subcommand("help")
    public void onHelp(final @NotNull CommandSender sender) {
        sender.sendMessage(MessageUtils.colorize("&6&lBetterHud &f&lv"+ BetterHud.getVersion() + " &7&o(( By ApiGames ))"));
        sender.sendMessage(" ");

        if(sender instanceof Player player) {
            JsonMessage.sendMessage(player, "/bh show","&8- &e/bh show (player/all) (hud) (display)", "&bShow the hud for the player\n\n&7Permission: &ebetterhud.command.show\n\n&f&lEXAMPLES:\n&a/bh show ApiGames example_hud ACTIONBAR");
            JsonMessage.sendMessage(player,"/bh hide" ,"&8- &e/bh hide (player/all) (hud)", "&bHide the hud from the player\n\n&7Permission: &ebetterhud.command.hide\n\n&f&lEXAMPLES:\n&a/bh hide ApiGames example_hud");
            JsonMessage.sendMessage(player,"/bh setValue","&8- &e/bh setValue (player/all) (hud) (element) (value)", "&bChange displayed value\n\n&7Permission: &ebetterhud.command.setvalue\n\n&f&lEXAMPLES:\n&a/bh setValue ApiGames example_hud example_text TEST123");
            JsonMessage.sendMessage(player, "/bh getValue","&8- &e/bh getValue (player) (hud) (element)", "&bGet the per-player value of the element\n\n&7Permission: &ebetterhud.command.getvalue\n\n&f&lEXAMPLES:\n&a/bh getValue ApiGames example_hud example_text");
            JsonMessage.sendMessage(player, "/bh resetValue","&8- &e/bh resetValue (player/all) (hud) (element)", "&bReset the per-player value\n\n&7Permission: &ebetterhud.command.resetvalue\n\n&f&lEXAMPLES:\n&a/bh resetValue ApiGames example_hud");
            JsonMessage.sendMessage(player, "/bh showElement","&8- &e/bh showElement (player/all) (hud) (element)", "&bShow the element for the player\n\n&7Permission: &ebetterhud.command.showelement\n\n&f&lEXAMPLES:\n&a/bh showElement ApiGames example_hud example_text");
            JsonMessage.sendMessage(player, "/bh hideElement","&8- &e/bh hideElement (player/all) (hud) (element)", "&bHide the element from the player\n\n&7Permission: &ebetterhud.command.hideelement\n\n&f&lEXAMPLES:\n&a/bh hideElement ApiGames example_hud example_text");
            JsonMessage.sendMessage(player, "/bh setX","&8- &e/bh setX (hud) (element) (value)", "&bSet x-coordinate of element\n\n&7Permission: &ebetterhud.command.setx\n\n&f&lEXAMPLES:\n&a/bh setX example_hud example_text 120");
            JsonMessage.sendMessage(player, "/bh reload","&8- &e/bh reload", "&bReload the plugin\n\n&7Permission: &ebetterhud.command.reload");
            JsonMessage.sendMessage(player, "/bh report-bug","&8- &e/bh report-bug", "&bGenerate report log\n\n&7Permission: &ebetterhud.command.report-bug");
            JsonMessage.sendMessage(player, "/bh extractTextures","&8- &e/bh extractTextures", "&bExtract default BetterHud textures\n\n&7Permission: &ebetterhud.command.extracttextures");
            sender.sendMessage(" ");
            sender.sendMessage(MessageUtils.colorize("&eTIP &8» &fTry &ahovering &fover the command to see more info and examples!"));

        } else {
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh show (player/all) (hud) (display) &7- Show the hud for the player"));
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh hide (player/all) (hud) &7- Hide the hud from the player"));
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh setValue (player/all) (hud) (element) (value) &7- Change displayed value"));
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh getValue (player) (hud) (element) &7- Get the per-player value of the element"));
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh resetValue (player/all) (hud) (element) &7- Reset the per-player value"));
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh showElement (player/all) (hud) (element) &7- Show the element for the player"));
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh hideElement (player/all) (hud) (element) &7- Hide the element from the player"));
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh setX (hud) (element) (value) &7- Set x-coordinate of element"));
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh reload &7- Reload the plugin"));
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh report-bug &7- Generate report log"));
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh extractTextures &7- Extract default BetterHud textures"));
        }

        sender.sendMessage(" ");
    }
}
