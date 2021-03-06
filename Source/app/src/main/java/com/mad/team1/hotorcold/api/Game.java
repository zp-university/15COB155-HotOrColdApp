package com.mad.team1.hotorcold.api;

import android.location.Location;

import java.util.List;

/**
 * @author Zack Pollard
 */
public interface Game {

    SimpleLocation getObjective();
    SimpleLocation getStartPoint();
    double getStartDistance();
    long getTravelDistance();
    long getStartTime();
    List<SimpleLocation> getLocationHistory();

    String calculateDistanceColour(Location currentLocation);
    int calculateScore();

    void endGame(Location finalLocation);
}
