package cz.apigames.betterhud.plugin.utils;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.api.Utils.ExceptionListener;

public class Exceptions implements ExceptionListener {

    @Override
    public void onException(Exception exception) {
        BetterHud.error(exception.getMessage(), exception);
    }

}
