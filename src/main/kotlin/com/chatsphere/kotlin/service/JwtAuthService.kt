package com.chatsphere.kotlin.service

import com.chatsphere.kotlin.config.properties.JWTProperties
import com.chatsphere.kotlin.model.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Service
class JwtAuthService(
    private val jwtProperties: JWTProperties
) {

    fun secretKey(): SecretKey {
        val keyBytes: ByteArray? = Base64.decode(jwtProperties.secretKey)
        return SecretKeySpec(keyBytes, "HmacSHA256")
    }

    fun createToken(user: User): String {
        val permissions: MutableMap<String, MutableList<String>> = mutableMapOf()
        permissions["roles"] = mutableListOf(user.role.name)

        return Jwts
            .builder()
            .setSubject(user.email)
            .setIssuer(jwtProperties.issuer)
            .claim("permissions", permissions)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + 3600 * 1000))
            .signWith(secretKey())
            .compact()
    }

    fun isTokenValid(token: String, user: User): Boolean {
        val email: String = extractEmailAddressFromToken(token)
        return (email == user.email && extractIssuer(token) == jwtProperties.issuer && !isTokenExpired(token))
    }

    private fun extractIssuer(token: String): String = extractClaim(token, Claims::getIssuer)

    fun extractEmailAddressFromToken(token: String): String = extractClaim(token, Claims::getSubject)

    private fun isTokenExpired(token: String): Boolean = extractExpiration(token).before(Date())

    private fun extractExpiration(token: String): Date = extractClaim(token, Claims::getExpiration)


    fun extractRolesFromToken(token: String): List<String> {
        val claims: Claims = extractAllClaims(token)
        val permissions = claims["permissions"] as? Map<*, *>
        val roles = permissions?.get("roles") as? List<*>

        return roles?.filterIsInstance<String>() ?: emptyList()
    }

    private fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims: Claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts
            .parserBuilder()
            .setSigningKey(secretKey())
            .build()
            .parseClaimsJws(token)
            .body
    }

}