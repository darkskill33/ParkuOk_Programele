package com.ismanieji.parkingosystema

data class RegisterRequest(
    val username: String,
    val password: String,
    val first_name: String,
    val last_name: String,
    val email: String
)
