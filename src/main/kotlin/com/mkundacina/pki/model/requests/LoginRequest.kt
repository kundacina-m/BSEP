package com.mkundacina.pki.model.requests

data class LoginRequest(
    val username: String,
    val password: String
)