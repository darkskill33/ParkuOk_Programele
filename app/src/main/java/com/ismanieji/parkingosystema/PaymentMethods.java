package com.ismanieji.parkingosystema;

public class PaymentMethods {
    private String type;
    private String identifier;
    private boolean isDefault;

    public PaymentMethods(String type, String identifier, boolean isDefault) {
        this.type = type;
        this.identifier = identifier;
        this.isDefault = isDefault;
    }

    public String getType(){
        return this.type;
    }

    public String getIdentifier(){
        return this.identifier;
    }

    public boolean isDefault(){
        return this.isDefault;
    }
}
