package cz.apigames.betterhud.api.elements;

import cz.apigames.betterhud.api.BetterHudAPI;
import cz.apigames.betterhud.api.utils.Condition;
import cz.apigames.betterhud.api.utils.MessageUtils;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.entity.Player;

import java.util.List;

public class IntegerElement extends Element {

    /**
     * Constructs an IntegerElement with the given display value
     *
     * @param name the internal name of this element used in config
     * @param x the x coordinate in pixels (0-1910)
     * @param y the y coordinate
     * @param scale the scale of this element
     * @param value the Integer value you want to display (can be placeholder, the result must be int)
     */
    public IntegerElement(String name, int x, int y, int scale, String value) {
        super(name, x, y, scale);
        this.value = value;

        config_name = "INTEGER";
    }

    /* --- METHODS --- */

    @Override
    public String getFor(Player player, String value) {

        StringBuilder output = new StringBuilder();

        //MESSAGE SETUP
        String message = isVisible(player) && Condition.checkFor(player, conditions) ? value : "";
        message = MessageUtils.translatePlaceholders(message,player, BetterHudAPI.getPlaceholders(player));

        int charCount = 0;
        int whitespace = 0;
        List<Character> chars = message.chars().mapToObj(e -> (char) e).toList();
        for(int i=0; i<chars.size(); i++) {

            //COLOR HANDLING
            if(String.valueOf(chars.get(i)).equalsIgnoreCase("&")) {
                if(MessageUtils.isLegacyColorCode(message, i)) {
                    output.append(message, i, i+2);
                    i += 1;
                    continue;
                }
            } else if(String.valueOf(chars.get(i)).equalsIgnoreCase("{")) {
                if(String.valueOf(chars.get(i+1)).equalsIgnoreCase("#")) {
                    if(MessageUtils.isHexColorCode(message, i)) {
                        output.append(message, i, i+9);
                        i += 8;
                        continue;
                    }
                }
            }

            //SPACE HANDLING
            if(Character.isSpaceChar(chars.get(i))) {
                whitespace += getScale()/2;
                continue;
            }

            if(BetterHudAPI.charactersInternalNames.containsKey(chars.get(i))) {

                FontImageWrapper characterImage = BetterHudAPI.fontImageCharacters.get(BetterHudAPI.charactersInternalNames.get(chars.get(i)) + "-" + y + "_" + scale);

                if(characterImage != null) {

                    if(characterImage.exists()) {

                        output.append(characterImage.applyPixelsOffset(ix + charCount + whitespace));

                        charCount++;

                    }

                }

            }

        }

        return MessageUtils.colorize(output + "&r");

    }
}
