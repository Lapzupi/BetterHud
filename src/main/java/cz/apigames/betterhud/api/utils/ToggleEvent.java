package cz.apigames.betterhud.api.utils;

import cz.apigames.betterhud.api.displays.DisplayType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ToggleEvent {

    @NotNull(message = "EventType is not valid or is null.")
    protected EventType eventType;

    @NotNull(message = "Display is not valid or is null.")
    protected DisplayType displayType;

    protected String optValue;

    @Min(message = "Value 'hide_after' must be a positive number", value = 0)
    protected int hideAfter;

    /**
     * Constructs a ToggleEvent with the given eventType and displayType
     *
     * @param eventType the type of the event
     * @param displayType the display type which will be used for displaying hud
     * @param hideAfter time in seconds when the hud will disappear (0 = never)
     *
     * @see EventType the list of valid event types
     */
    public ToggleEvent(EventType eventType, DisplayType displayType, int hideAfter) {

        this.eventType = eventType;
        this.displayType = displayType;
        this.hideAfter = hideAfter;

    }

    /**
     * Constructs a ToggleEvent with the given eventType and displayType
     *
     * @param eventType the type of the event
     * @param displayType the display type which will be used for displaying hud
     * @param value optional value that is needed for event types: COMMAND, GAMEMODE_CHANGE
     * @param hideAfter time in seconds when the hud will disappear (0 = never)
     *
     * @see EventType the list of valid event types
     */
    public ToggleEvent(EventType eventType, DisplayType displayType, String value, int hideAfter) {

        this.eventType = eventType;
        this.displayType = displayType;
        this.hideAfter = hideAfter;
        this.optValue = value;

    }

    public DisplayType getDisplayType() {
        return displayType;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getOptValue() {
        return optValue;
    }

    public int getHideAfter() {
        return hideAfter;
    }

    public enum EventType {

        PLAYER_JOIN,
        COMMAND,
        GAMEMODE_CHANGE,
        DAMAGE_BY_PLAYER,
        DAMAGE_BY_ENTITY,
        DAMAGE_OTHER

    }
}
