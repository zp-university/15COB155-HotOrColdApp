package com.mad.team1.hotorcold.api;

import android.location.Location;

import java.util.List;

/**
 * @author Zack Pollard
 */
public interface Game {

    SimpleLocation getObjective();
    Location getStartPoint();
    long getStartTime();
    List<SimpleLocation> getLocationHistory();

    float calculateDistanceToObjective(Location currentLocation, DistanceUnit unit);
    String calculateDistanceColour(Location currentLocation);
}
