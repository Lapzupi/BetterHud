package cz.apigames.betterhud.api.displays;

import cz.apigames.betterhud.api.Hud;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Display {

    protected Integer taskID;
    protected Player player;
    protected Hud hud;
    protected static List<Display> displays = new ArrayList<>();

    public Display(Player player, Hud hud) {

        this.player = player;
        this.hud = hud;
        displays.add(this);

        init();
    }

    public static @Nullable Display createDisplay(Player player, Hud hud, @NotNull DisplayType displayType) {

        if(displayType.equals(DisplayType.ACTIONBAR)) {
            return new ActionBarDisplay(player, hud);
        } else if(displayType.equals(DisplayType.BOSSBAR)) {
            return new BossBarDisplay(player, hud);
        } else {
            return null;
        }

    }

    public abstract void init();

    public abstract void render();

    public void destroy() {

        this.player = null;
        this.hud = null;
        Bukkit.getScheduler().cancelTask(taskID);

        displays.remove(this);

    }

    public Player getPlayer() {
        return player;
    }

    public Hud getHud() {
        return hud;
    }

    public static List<Display> getDisplays(Player player) {
        return displays.stream().filter(display -> display.getPlayer().equals(player)).toList();
    }

    public static List<Display> getDisplays(Hud hud) {
        return displays.stream().filter(display -> display.getHud().equals(hud)).toList();
    }

    public static List<Display> getDisplays(Player player, Hud hud) {
        return displays.stream().filter(display -> display.getPlayer().equals(player) && display.getHud().equals(hud)).toList();
    }

    public static List<Display> getDisplays() {
        return displays;
    }

    public static void destroyAll() {
        new ArrayList<>(displays).forEach(Display::destroy);
    }

}
