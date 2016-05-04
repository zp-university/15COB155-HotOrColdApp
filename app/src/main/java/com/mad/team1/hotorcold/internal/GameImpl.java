package com.mad.team1.hotorcold.internal;

import android.graphics.Color;
import android.location.Location;

import com.mad.team1.hotorcold.api.DistanceUnit;
import com.mad.team1.hotorcold.api.Game;
import com.mad.team1.hotorcold.api.SimpleLocation;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @author Zack Pollard
 */
public class GameImpl implements Game {

    /**
     * Matt Weaver, [27.04.16 23:00]
     255, 67, 67 red

     Matt Weaver, [27.04.16 23:01]
     67, 182, 255 blue
     */

    private final SimpleLocation objective;
    private final SimpleLocation startPoint;
    private double startDistance;
    private long startTime;
    private List<SimpleLocation> locationHistory;

    private GameImpl(SimpleLocation startPoint, int maxDistance, DistanceUnit unitPreference) {

        this.startPoint = startPoint;
        this.startTime = System.currentTimeMillis();
        this.locationHistory = new LinkedList<>();
        objective = generateObjective(startPoint, maxDistance, unitPreference);
        this.startDistance = calculateDistanceToObjective(startPoint, DistanceUnit.METERS);
    }

    @Override
    public SimpleLocation getObjective() {
        return objective;
    }

    @Override
    public SimpleLocation getStartPoint() {
        return startPoint;
    }

    @Override
    public double getStartDistance() {
        return startDistance;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public List<SimpleLocation> getLocationHistory() {
        return locationHistory;
    }

    private SimpleLocation generateObjective(SimpleLocation startPoint, int maxDistance, DistanceUnit unitPreference){

        Random random = new Random();

        double convertedMaxDistance;

        switch(unitPreference) {

            case METERS: convertedMaxDistance = maxDistance; break;
            case YARDS: convertedMaxDistance = maxDistance * 0.9144; break;
            case NAUTICAL_MILES: convertedMaxDistance = maxDistance * 1852; break;
            case MILES: convertedMaxDistance = maxDistance * 1609.34; break;
            case KILOMETERS: convertedMaxDistance = maxDistance * 1000; break;
            default: convertedMaxDistance = Float.valueOf("AGAIN, HOW?!?!?");
        }

        // Convert radius from meters to degrees
        double radiusInDegrees = convertedMaxDistance / 111000F;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(startPoint.getLatitude());

        double foundLongitude = new_x + startPoint.getLongitude();
        double foundLatitude = y + startPoint.getLatitude();

        return SimpleLocationImpl.createNewSimpleLocation(foundLongitude, foundLatitude);
    }

    private float calculateDistanceToObjective(SimpleLocation currentLocation, DistanceUnit unit) {

        double objectiveLatitude = getObjective().getLatitude();
        double objectiveLongitude = getObjective().getLongitude();
        double currentLatitude = currentLocation.getLatitude();
        double currentLongitude = currentLocation.getLongitude();

        double earthRadius = 6371000;
        double dLat = Math.toRadians(objectiveLatitude-currentLatitude);
        double dLng = Math.toRadians(objectiveLongitude-currentLongitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(currentLatitude)) * Math.cos(Math.toRadians(objectiveLatitude)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        switch(unit) {

            case METERS: return dist;
            case YARDS: return dist * 1.09361F;
            case NAUTICAL_MILES: return dist * 0.000539957F;
            case MILES: return dist * 0.000621371F;
            case KILOMETERS: return dist * 0.001F;
            default: return Float.valueOf("HOW!?!");
        }
    }

    private double calculatePercentageDistance(SimpleLocation currentLocation, double startDistance) {

        double distance = calculateDistanceToObjective(currentLocation, DistanceUnit.METERS);
        double difference = startDistance - distance;

        return difference / startDistance;
    }

    @Override
    public String calculateDistanceColour(Location currentLocation) {

        SimpleLocation simpleLocation = SimpleLocationImpl.createNewSimpleLocation(currentLocation);

        double percentageDistance = calculatePercentageDistance(simpleLocation, startDistance);

        if(percentageDistance < 0) {

            percentageDistance = 0;
            startDistance = calculateDistanceToObjective(simpleLocation, DistanceUnit.METERS);
        }

        int red = ((int)(188F * percentageDistance)) + 67;
        int green = 182 - ((int)(115F * percentageDistance));
        int blue = 255 - ((int)(188F * percentageDistance));

        String hexRed = Integer.toHexString(red).toUpperCase().substring(0, 2);
        String hexGreen = Integer.toHexString(green).toUpperCase().substring(0, 2);
        String hexBlue = Integer.toHexString(blue).toUpperCase().substring(0, 2);

        return "#" + hexRed + hexGreen + hexBlue;
    }

    @Override
    public int calculateScore(Location currentLocation) {

        SimpleLocation simpleLocation = SimpleLocationImpl.createNewSimpleLocation(currentLocation);
        double originalDistance = calculateDistanceToObjective(startPoint, DistanceUnit.METERS);
        double currentDistance = calculateDistanceToObjective(simpleLocation, DistanceUnit.METERS);

        if((originalDistance - currentDistance) <= 50) {

            return 0;
        }

        int points = (int) (0.1 * ((currentDistance - 50) + Math.pow((0.01 * currentDistance), 2)));

        double percentageDistance = calculatePercentageDistance(simpleLocation, originalDistance);

        return (int) (points * percentageDistance);
    }

    protected static Game createNewGame(Location currentLocation, int maxDistance, DistanceUnit unitPreference) {

        return new GameImpl(SimpleLocationImpl.createNewSimpleLocation(currentLocation), maxDistance, unitPreference);
    }
}
