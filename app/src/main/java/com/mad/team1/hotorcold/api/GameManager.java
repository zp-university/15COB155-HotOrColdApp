package com.mad.team1.hotorcold.api;

import android.location.Location;

import com.mad.team1.hotorcold.internal.GameManagerImpl;

/**
 * @author Zack Pollard
 */
public abstract class GameManager {

    public abstract Game getCurrentGame();
    public abstract Game getPreviousGame();
    public abstract Game startNewGame(Location currentPosition, int maxDistance, DistanceUnit unitPreference);
    public abstract Game endCurrentGame(Location finalPosition);

    public static GameManager createGameManager() {

        return new GameManagerImpl();
    }
}
