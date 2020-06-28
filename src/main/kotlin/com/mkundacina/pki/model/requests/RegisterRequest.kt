package com.mkundacina.pki.model.requests

data class RegisterRequest(
        val username: String,
        val email: String,
        val password: String
)