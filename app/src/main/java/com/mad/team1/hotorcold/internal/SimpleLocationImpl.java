package com.mad.team1.hotorcold.internal;

import com.mad.team1.hotorcold.api.SimpleLocation;

/**
 * @author Oliver Marshall
 */
public class SimpleLocationImpl implements SimpleLocation {

    private double longitude;
    private double latitude;

    private SimpleLocationImpl(double longitude, double latitude) {

        this.longitude = longitude;
        this.latitude = latitude;
    }

    protected static SimpleLocation CreateNewSimpleLocation(double longitude, double latitude) {

        return new SimpleLocationImpl(longitude, latitude);
    }

    @Override
    public double getLongitude() {

        return longitude;
    }

    @Override
    public double getLatitude() {

        return latitude;
    }
}