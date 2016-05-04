package com.mad.team1.hotorcold.internal;

import android.location.Location;

import com.mad.team1.hotorcold.api.DistanceUnit;
import com.mad.team1.hotorcold.api.Game;
import com.mad.team1.hotorcold.api.GameManager;

/**
 * @author Zack Pollard
 */
public class GameManagerImpl extends GameManager {

    private Game currentGame;
    private Game previousGame;

    @Override
    public Game getCurrentGame() {
        return currentGame;
    }

    @Override
    public Game getPreviousGame() {
        return previousGame;
    }

    @Override
    public Game startNewGame(Location currentPosition, int maxDistance, DistanceUnit unitPreference) {

        if(currentGame == null) {

            currentGame = GameImpl.createNewGame(currentPosition, maxDistance, unitPreference);
            return currentGame;
        }

        return null;
    }

    @Override
    public Game endCurrentGame(Location finalLocation) {
        currentGame.endGame(finalLocation);
        previousGame = currentGame;
        currentGame = null;
        return previousGame;
    }
}
