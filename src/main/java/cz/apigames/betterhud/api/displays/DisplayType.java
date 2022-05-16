package cz.apigames.betterhud.api.displays;

public enum DisplayType {
    ACTIONBAR,
    BOSSBAR,
    CHAT;

    public static DisplayType getDisplayType(Display display) {
        if(display instanceof ActionBarDisplay) {
            return ACTIONBAR;
        }

        if(display instanceof BossBarDisplay) {
            return BOSSBAR;
        }

        return null;
    }

}
