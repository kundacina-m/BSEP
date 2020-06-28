package com.mkundacina.pki.security.auth

import com.mkundacina.pki.security.TokenUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class TokenAuthenticationFilter(private val tokenUtils: TokenUtils, private val userDetailsService: UserDetailsService) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {

        val authToken = tokenUtils.getToken(request)

        if (authToken != null) {
            // uzmi username iz tokena
            val username = tokenUtils.getUsernameFromToken(authToken)
            if (username != null) {

                // uzmi user-a na osnovu username-a
                val userDetails = userDetailsService.loadUserByUsername(username)

                // proveri da li je prosledjeni token validan
                if (tokenUtils.validateToken(authToken, userDetails)) {

                    // kreiraj autentifikaciju
                    val authentication = TokenBasedAuthentication(userDetails)
                    authentication.token = authToken
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        }
        filterChain.doFilter(request, response)
    }

}