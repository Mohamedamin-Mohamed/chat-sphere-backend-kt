package com.chatsphere.kotlin.config

import com.chatsphere.kotlin.service.JwtAuthService
import com.chatsphere.kotlin.service.UserService
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter

@Configuration
@EnableWebSecurity
class JwtAuthFilter(
    private val jwtAuthService: JwtAuthService,
    private val userService: UserService
) : OncePerRequestFilter() {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(JwtAuthFilter::class.java)
    }

    override fun doFilterInternal(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token: String? = getTokenFromHeader(httpServletRequest)

        if (token == null) {
            filterChain.doFilter(httpServletRequest, httpServletResponse)
            return
        }
        try {
            val userEmail = jwtAuthService.extractEmailAddressFromToken(token)
            if (userEmail.isNotEmpty() && SecurityContextHolder.getContext().authentication == null) {
                val user = userService.findRawUserEmail(userEmail)
                if (jwtAuthService.isTokenValid(token, user)) {
                    val rolesFromToken = jwtAuthService.extractRolesFromToken(token)

                    val grantedAuthorities: List<GrantedAuthority> = rolesFromToken
                        /*
                         Since we are using method-level security with hasRole(String role),
                         we need to prefix each role name with "ROLE_" when setting up the authorities.
                         This is because Spring Security adds the "ROLE_" prefix internally when checking roles with hasRole(),
                         so we only pass the role name (like "ADMIN") in the annotation.
                         */
                        .map { SimpleGrantedAuthority("ROLE_$it") }
                        .map { it }

                    val authToken = UsernamePasswordAuthenticationToken(user, null, grantedAuthorities)
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(httpServletRequest)

                    SecurityContextHolder.getContext().authentication = authToken
                }
            }
        } catch (exp: ExpiredJwtException) {
            logger.warn("JWT token has expired ${exp.message}")
            httpServletResponse.status = HttpServletResponse.SC_UNAUTHORIZED
            httpServletResponse.writer.write("TOKEN_EXPIRED")
        } catch (exp: MalformedJwtException) {
            logger.warn("JWT token is malformed ${exp.message}")
            httpServletResponse.status = HttpServletResponse.SC_BAD_REQUEST
        } catch (exp: Exception) {
            logger.warn("Unknown exception was thrown ${exp.message}")
            httpServletResponse.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse)
    }

    private fun getTokenFromHeader(httpServletRequest: HttpServletRequest): String? {
        val bearerToken = httpServletRequest.getHeader("Authorization")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return null
    }
}