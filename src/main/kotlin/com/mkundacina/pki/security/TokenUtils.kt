package com.mkundacina.pki.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.HttpServletRequest

@Component
class TokenUtils {
    @Value("bsep")
    private val APP_NAME: String? = null

    @Value("secretKeyThatIsCool")
    var SECRET: String? = null

    @Value("240000")
    val expiredIn = 0

    @Value("Authorization")
    private val AUTH_HEADER: String? = null
    private val SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512

    fun generateToken(username: String?): String {
        return Jwts.builder()
                .setIssuer(APP_NAME)
                .setSubject(username)
                .setAudience(AUDIENCE_MOBILE)
                .setIssuedAt(Date())
                .setExpiration(generateExpirationDate())
                .signWith(SIGNATURE_ALGORITHM, SECRET).compact()
    }

    private fun generateExpirationDate(): Date {
        return Date(Date().time + expiredIn)
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = getUsernameFromToken(token)
        return username != null && username == userDetails.username
    }

    fun getUsernameFromToken(token: String): String? =
            try {
                val claims = getAllClaimsFromToken(token)
                claims!!.subject
            } catch (e: Exception) {
                null
            }

    fun getToken(request: HttpServletRequest): String? {
        val authHeader = getAuthHeaderFromHeader(request)
        return if (authHeader.startsWith("Bearer ")) {
            authHeader.substring(7)
        } else null
    }

    fun getAuthHeaderFromHeader(request: HttpServletRequest): String =
            request.getHeader(AUTH_HEADER)

    private fun getAllClaimsFromToken(token: String): Claims? =
            try {
                Jwts.parser()
                        .setSigningKey(SECRET)
                        .parseClaimsJws(token)
                        .body
            } catch (e: Exception) {
                null
            }

    companion object {
        const val AUDIENCE_MOBILE = "mobile"
    }
}