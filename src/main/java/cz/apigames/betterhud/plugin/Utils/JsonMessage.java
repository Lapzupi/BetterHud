package cz.apigames.betterhud.plugin.utils;

import cz.apigames.betterhud.api.utils.MessageUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class JsonMessage {

    public static void sendMessage(@NotNull Player player, String suggestCommand, String message, String hover) {

        TextComponent text = new TextComponent(MessageUtils.colorize(message));
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(MessageUtils.colorize(hover))));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestCommand));
        player.spigot().sendMessage(text);

    }
}
