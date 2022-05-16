package cz.apigames.betterhud.api.utils.listeners;

import cz.apigames.betterhud.api.BetterHudAPI;
import org.bukkit.Bukkit;

public class ListenerRegister {

    public static void registerListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(new ItemsAdderLoad(), BetterHudAPI.getPlugin());
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), BetterHudAPI.getPlugin());
    }

}
