package com.ismanieji.parkingosystema;

public class Reservation {
    private String locationName;
    private long startTime;
    private long endTime;

    public Reservation(String locationName, long startTime) {
        this.locationName = locationName;
        this.startTime = startTime;
        this.endTime = -1;
    }

    public Reservation(String locationName, long startTime, long endTime) {
        this.locationName = locationName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getLocationName() {
        return locationName;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public boolean isActive() {
        return endTime < 0;
    }
}


