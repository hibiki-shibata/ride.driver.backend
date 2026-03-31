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
import com.ride.driver.backend.shared.auth.domain.ServiceType
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
        val jwtToken: String = getJwtTokenFromRequest(request)
        if(isAlreadyAuthenticated() || !isTokenValid(request, jwtToken)) {
            filterChain.doFilter(request, response) // Let it through so that it'll be caught by Spring Security's exception handling for unauthenticated access
            return
        }        
        val accessTokenClaim: AccessTokenClaim = jwtTokenService.extractAccessTokenClaim(jwtToken)
        val authentication = UsernamePasswordAuthenticationToken(
            accessTokenClaim, // principal
            null, // No credentials, I use JWT auth instead
            accessTokenClaim.accountRoles.map { SimpleGrantedAuthority(it.name) }
        ).apply {
            details = WebAuthenticationDetailsSource().buildDetails(request) // Add web details(e.g. IP, session info)
        }
        SecurityContextHolder.getContext().authentication = authentication // Set the authentication in the security context
        logger.debug("event=authentication_successful accountId={} roles={}", accessTokenClaim.accountId, accessTokenClaim.accountRoles.joinToString(","))
        filterChain.doFilter(request, response)
    }

    private fun isAlreadyAuthenticated(): Boolean {
        return SecurityContextHolder.getContext().authentication != null
    }

    private fun isTokenValid(request: HttpServletRequest, jwtToken: String?): Boolean {
        val expectedServiceType: ServiceType? = resolveExpectedServiceTypeFromRequest(request)
        if (jwtToken.isNullOrBlank() || expectedServiceType == null) return false        
        return jwtTokenService.isTokenValid(
            token = jwtToken,
            expectedServiceType = expectedServiceType
        )        
    }

    private fun getJwtTokenFromRequest(request: HttpServletRequest): String {
        val authHeader: String? = request.getHeader("Authorization")
        if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
            return ""
        }
        return authHeader.substringAfter("Bearer ").trim()
    }

    private fun resolveExpectedServiceTypeFromRequest(request: HttpServletRequest): ServiceType? {
        val path = request.servletPath
        return when {
            path.startsWith("/api/v1/consumers") -> ServiceType.CONSUMER
            path.startsWith("/api/v1/couriers") -> ServiceType.COURIER
            path.startsWith("/api/v1/merchants") -> ServiceType.MERCHANT
            else -> null
        }
    }
}