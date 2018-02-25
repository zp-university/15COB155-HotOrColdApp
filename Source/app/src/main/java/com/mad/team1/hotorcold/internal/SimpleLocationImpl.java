package com.mad.team1.hotorcold.internal;

import android.location.Location;

import com.mad.team1.hotorcold.api.SimpleLocation;

/**
 * @author Oliver Marshall
 */
public class SimpleLocationImpl implements SimpleLocation {

    private final double longitude;
    private final double latitude;

    private SimpleLocationImpl(double longitude, double latitude) {

        this.longitude = longitude;
        this.latitude = latitude;
    }

    protected static SimpleLocation createNewSimpleLocation(double longitude, double latitude) {

        return new SimpleLocationImpl(longitude, latitude);
    }

    protected static SimpleLocation createNewSimpleLocation(Location location) {

        return new SimpleLocationImpl(location.getLongitude(), location.getLatitude());
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