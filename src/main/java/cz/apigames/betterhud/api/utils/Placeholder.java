package cz.apigames.betterhud.api.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Placeholder {

    private final String placeholder;
    private final String value;

    public Placeholder(String placeholder, String value) {
        this.placeholder = placeholder;
        this.value = value;
    }

    private String replacePlaceholder(String message) {

        if (placeholder == null || value == null) {
            return message;
        }

        return message.replace(placeholder, value);
    }

    @Contract(pure = true)
    public static String replacePlaceholders(@NotNull List<Placeholder> placeholderList, String message) {
        for (Placeholder placeholder : placeholderList) {
            message = placeholder.replacePlaceholder(message);
        }
        return message;
    }

    @Contract("_, _ -> param2")
    public static @NotNull List<String> replacePlaceholders(List<Placeholder> placeholderList, List<String> message) {
        List<String> copy = new ArrayList<>(message);
        message.clear();
        if (placeholderList == null || placeholderList.isEmpty())
            return message;


        for (int i = 0; i < copy.size(); i++) {

            message.add(copy.get(i));

            for (Placeholder placeholder : placeholderList) {

                if (copy.get(i).contains(placeholder.placeholder)) {
                    String replaced = placeholder.replacePlaceholder(copy.get(i));

                    if (replaced.contains("\n")) {

                        String[] split = replaced.split("\n");

                        message.set(i, split[0]);

                        for (int i2 = 1; i2 < split.length; i2++) {
                            message.add(placeholder.replacePlaceholder(split[i2]));
                        }

                    } else {
                        message.set(i, replaced);
                    }
                }

            }
        }


        return message;
    }

    @Override
    public String toString() {
        return "Placeholder{" +
                "placeholder='" + placeholder + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
