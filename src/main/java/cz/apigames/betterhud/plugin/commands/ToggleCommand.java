package cz.apigames.betterhud.plugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import cz.apigames.betterhud.api.BetterHudAPI;
import cz.apigames.betterhud.api.utils.MessageUtils;
import cz.apigames.betterhud.api.utils.ToggleEvent;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author sarhatabaot
 */
@CommandAlias("betterhud|bh")
public class ToggleCommand extends BaseCommand {
    private static String toggleOn;
    private static String toggleOff;

    @Subcommand("toggle")
    @Description("Custom toggle command for BetterHud")
    public void onToggle(final Player player, final String value) {
        BetterHudAPI.getLoadedHuds().forEach(hud -> {

            Optional<ToggleEvent> optEvent = hud.getEvents().stream().filter(toggleEvent -> toggleEvent.getEventType().equals(ToggleEvent.EventType.COMMAND) && toggleEvent.getOptValue().equalsIgnoreCase(value)).findFirst();

            optEvent.ifPresent(toggleEvent -> {

                if (!hud.isVisible(player)) {
                    hud.renderFor(player, toggleEvent.getDisplayType(), toggleEvent.getHideAfter(), true);
                    if (toggleOn != null && !toggleOff.equals("")) {
                        player.sendMessage(MessageUtils.colorize(toggleOn).replace("{hudName}", hud.getName()));
                    }
                } else {
                    hud.hide(player);
                    if (toggleOff != null && !toggleOff.equals("")) {
                        player.sendMessage(MessageUtils.colorize(toggleOff).replace("{hudName}", hud.getName()));
                    }
                }

            });
        });
    }

    public static void setEnableMessage(String message) {
        toggleOn = message;
    }

    public static void setDisableMessage(String message) {
        toggleOff = message;
    }

}
