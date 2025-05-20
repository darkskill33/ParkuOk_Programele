package com.ismanieji.parkingosystema;


public class PaymentRecord {
    private String method;
    private int cost;
    private String location;
    private long timestamp;

    public PaymentRecord(String method, int cost, String location, long timestamp) {
        this.method = method;
        this.cost = cost;
        this.location = location;
        this.timestamp = timestamp;
    }

    public String getMethod() { return method; }
    public int getCost() { return cost; }
    public String getLocation() { return location; }
    public long getTimestamp() { return timestamp; }
}
