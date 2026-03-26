package com.ride.driver.backend.middleware

import org.springframework.stereotype.Component
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.filter.OncePerRequestFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.FilterChain
import com.ride.driver.backend.shared.auth.service.JwtTokenService
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Component 
class JwtFilter(
    private val jwtTokenService: JwtTokenService,
) : OncePerRequestFilter() {
    private val logger = LoggerFactory.getLogger(JwtFilter::class.java)

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        return path.startsWith("/api/v1/merchants/auth") || 
               path.startsWith("/api/v1/consumers/auth") || 
               path.startsWith("/api/v1/couriers/auth")
    }
    
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
       if (SecurityContextHolder.getContext().authentication != null) {
            filterChain.doFilter(request, response) // Just let it pass through when it's already authenticated
            return
        }        
        val jwtToken: String? = getJwtTokenFromRequest(request)
        if (jwtToken.isNullOrBlank() || !jwtTokenService.isTokenValid(jwtToken)) {
            filterChain.doFilter(request, response) // Let it pass through and eventually be caught by Spring Security's exception handling for unauthenticated access
            return
        }
        val AccessTokenClaim: AccessTokenClaim = jwtTokenService.extractAccessTokenClaim(jwtToken)
        val authentication = UsernamePasswordAuthenticationToken(
            AccessTokenClaim, // principal
            null, // No credentials, I use JWT auth instead
            AccessTokenClaim.accountRoles.map { SimpleGrantedAuthority(it.name) }
        ).apply {
            details = WebAuthenticationDetailsSource().buildDetails(request) // Add web details(e.g. IP, session info)
        }
        SecurityContextHolder.getContext().authentication = authentication // Set the authentication in the security context
        logger.debug("Authenticated request for account ID ${AccessTokenClaim.accountId}, Request URI: ${request.requestURI}")
        filterChain.doFilter(request, response)
    }

    private fun getJwtTokenFromRequest(request: HttpServletRequest): String? {
        val authHeader: String? = request.getHeader("Authorization")
        if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
            return null
        }
        return authHeader.substringAfter("Bearer ").trim()
    }
}