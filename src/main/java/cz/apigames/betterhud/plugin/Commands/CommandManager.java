package cz.apigames.betterhud.plugin.commands;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.api.BetterHudAPI;
import cz.apigames.betterhud.api.displays.Display;
import cz.apigames.betterhud.api.displays.DisplayType;
import cz.apigames.betterhud.api.elements.Element;
import cz.apigames.betterhud.api.Hud;
import cz.apigames.betterhud.api.utils.MessageUtils;
import cz.apigames.betterhud.api.utils.ToggleCommand;
import cz.apigames.betterhud.plugin.utils.ConfigManager;
import cz.apigames.betterhud.plugin.utils.FileUtils;
import cz.apigames.betterhud.plugin.utils.JsonMessage;
import cz.apigames.betterhud.plugin.utils.TextureExtractor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
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

public class CommandManager implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(command.getName().equalsIgnoreCase("bh") || command.getName().equalsIgnoreCase("betterhud")) {

            if(args.length > 0) {

                //SHOW
                if(args[0].equalsIgnoreCase("show")) {

                    Bukkit.getScheduler().runTaskAsynchronously(BetterHud.getPlugin(), () -> {

                        if(sender.hasPermission("betterhud.command.show")) {

                            if(args.length > 1) {

                                if(args.length > 2) {

                                    if(args.length > 3) {

                                        if(args[1].equalsIgnoreCase("all")) {
                                            //ALL

                                            if(BetterHud.getAPI().hudExists(args[2])) {

                                                try {
                                                    DisplayType displayType = DisplayType.valueOf(args[3]);

                                                    for(Player target : Bukkit.getOnlinePlayers()) {

                                                        try {
                                                            BetterHud.getAPI().getHud(args[2]).renderFor(target, displayType);
                                                        } catch (IllegalStateException e) {
                                                            sender.sendMessage(BetterHud.getMessage("show-error"));
                                                            return;
                                                        }

                                                    }

                                                    sender.sendMessage(BetterHud.getMessage("show-all")
                                                            .replace("{hudName}", args[2]));

                                                } catch (IllegalArgumentException e) {
                                                    sender.sendMessage(BetterHud.getMessage("unknown-display"));
                                                }

                                            } else {
                                                sender.sendMessage(BetterHud.getMessage("unknown-hud"));
                                            }

                                        } else {
                                            //TARGET

                                            Player target = Bukkit.getPlayerExact(args[1]);
                                            if(target != null) {
                                                //VALID PLAYER

                                                if(BetterHud.getAPI().hudExists(args[2])) {

                                                    try {
                                                        DisplayType displayType = DisplayType.valueOf(args[3]);

                                                        try {
                                                            if(BetterHud.getAPI().getHud(args[2]).renderFor(target, displayType)) {
                                                                sender.sendMessage(BetterHud.getMessage("show-player")
                                                                        .replace("{hudName}", args[2]).replace("{player}", target.getName()));
                                                            } else {
                                                                sender.sendMessage(BetterHud.getMessage("show-condition-error"));
                                                            }
                                                        } catch (IllegalStateException | NumberFormatException e) {

                                                            if(e instanceof NumberFormatException) {
                                                                sender.sendMessage(BetterHud.getMessage("show-error").replace("{error}", ((NumberFormatException) e).getMessage()));
                                                            } else {
                                                                sender.sendMessage(BetterHud.getMessage("show-display-active"));
                                                            }
                                                        }

                                                    } catch (IllegalArgumentException e) {
                                                        sender.sendMessage(BetterHud.getMessage("unknown-display"));
                                                    }

                                                } else {
                                                    sender.sendMessage(BetterHud.getMessage("unknown-hud"));
                                                }

                                            } else {
                                                sender.sendMessage(BetterHud.getMessage("unknown-player"));
                                            }
                                        }

                                    } else {
                                        sender.sendMessage(BetterHud.getMessage("no-display"));
                                    }

                                } else {
                                    sender.sendMessage(BetterHud.getMessage("no-hud"));
                                }

                            } else {
                                sender.sendMessage(BetterHud.getMessage("no-player"));
                            }

                        } else {
                            sender.sendMessage(BetterHud.getMessage("no-permission"));
                        }

                    });

                }

                //HIDE
                else if(args[0].equalsIgnoreCase("hide")) {

                    if(sender.hasPermission("betterhud.command.hide")) {

                        if(args.length > 1) {

                            if(args.length > 2) {

                                if(args[1].equalsIgnoreCase("all")) {
                                    //ALL

                                    if(BetterHud.getAPI().hudExists(args[2])) {

                                        for(Player target : Bukkit.getOnlinePlayers()) {
                                            BetterHud.getAPI().getHud(args[2]).hide(target);
                                        }

                                        sender.sendMessage(BetterHud.getMessage("hide-all")
                                                .replace("{hudName}", args[2]));

                                    } else {
                                        sender.sendMessage(BetterHud.getMessage("unknown-hud"));
                                    }

                                } else {
                                    //TARGET

                                    Player target = Bukkit.getPlayerExact(args[1]);
                                    if(target != null) {
                                        //VALID PLAYER

                                        if(BetterHud.getAPI().hudExists(args[2])) {

                                            BetterHud.getAPI().getHud(args[2]).hide(target);

                                            sender.sendMessage(BetterHud.getMessage("hide-player")
                                                    .replace("{hudName}", args[2]).replace("{player}", target.getName()));

                                        } else {
                                            sender.sendMessage(BetterHud.getMessage("unknown-hud"));
                                        }

                                    } else {
                                        sender.sendMessage(BetterHud.getMessage("unknown-player"));
                                    }
                                }

                            } else {
                                sender.sendMessage(BetterHud.getMessage("no-hud"));
                            }

                        } else {
                            sender.sendMessage(BetterHud.getMessage("no-player"));
                        }

                    } else {
                        sender.sendMessage(BetterHud.getMessage("no-permission"));
                    }

                }
            }

        }

        return true;
    }

    public static boolean isNumber(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
