package cz.apigames.betterhud.api.elements;

public enum Alignment {

    LEFT,
    CENTER,
    RIGHT;

    public static Alignment get(String str) {

        if (str == null) {
            return Alignment.LEFT;
        }

        if (str.equalsIgnoreCase("left")) {
            return Alignment.LEFT;
        }
        if (str.equalsIgnoreCase("center")) {
            return Alignment.CENTER;
        }
        if (str.equalsIgnoreCase("right")) {
            return Alignment.RIGHT;
        }
        return Alignment.LEFT;


    }

}
