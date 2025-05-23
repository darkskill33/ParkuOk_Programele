package com.ismanieji.parkingosystema;

public class UpdateProfileRequest {
    private String email;
    private String password;

    public UpdateProfileRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
