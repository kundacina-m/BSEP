package com.mkundacina.pki.security.auth

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails

class TokenBasedAuthentication(private val principle: UserDetails) : AbstractAuthenticationToken(principle.authorities) {

    var token: String? = null

    override fun isAuthenticated() = true
    override fun getCredentials() = token!!
    override fun getPrincipal(): UserDetails = principle

}