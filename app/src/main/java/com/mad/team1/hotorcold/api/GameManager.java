package com.mad.team1.hotorcold.api;

import android.location.Location;

/**
 * @author Zack Pollard
 */
public interface GameManager {

    Game getCurrentGame();
    Game startNewGame(Location currentPosition, int maxDistance, DistanceUnit unitPreference);
    Game endCurrentGame();
}
