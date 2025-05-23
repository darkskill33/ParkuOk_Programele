package com.ismanieji.parkingosystema;

public class Reservation {
    private String locationName;
    private long startTime;
    private long endTime;
    private String carPlate;  // NEW

    // Constructor for active reservation (no end time yet)
    public Reservation(String locationName, long startTime, String carPlate) {
        this.locationName = locationName;
        this.startTime = startTime;
        this.endTime = -1;
        this.carPlate = carPlate;
    }

    // Constructor for completed reservation
    public Reservation(String locationName, long startTime, long endTime, String carPlate) {
        this.locationName = locationName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.carPlate = carPlate;
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

    public String getCarPlate() {  // NEW getter
        return carPlate;
    }

    public boolean isActive() {
        return endTime < 0;
    }
}
