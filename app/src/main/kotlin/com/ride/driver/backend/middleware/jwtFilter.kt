package com.ride.driver.backend.middleware

import org.springframework.stereotype.Component
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.filter.OncePerRequestFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.FilterChain
import com.ride.driver.backend.services.JwtTokenService

@Component 
class JwtFilter(
    private val jwtTokenService: JwtTokenService,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
    try{
        val authHeader: String = request.getHeader("Authorization") ?: throw Exception("Authorization header missing")
        val jwtToken: String = authHeader.substringAfter("Bearer ") ?: throw Exception("Bearer token missing in Authorization header")
        if (SecurityContextHolder.getContext().authentication !== null && !jwtTokenService.isTokenValid(jwtToken)) {
            val username: String = jwtTokenService.extractUsername(jwtToken)
            val userRoles: List<String> = jwtTokenService.extractRoles(jwtToken)
            val userDetails = userDetailsService.loadUserByUsername(username)
            val authenticationToken = UsernamePasswordAuthenticationToken(
                userDetails, // Principal
                null, // Credentials
                userRoles.map { SimpleGrantedAuthority(it) } // Authorities
            )
            authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request) // Add web details like IP, session info in the context
            SecurityContextHolder.getContext().authentication = authenticationToken // It pass data so that business logic can use it
        } else throw Exception("Invalid JWT Token or already authenticated")
        filterChain.doFilter(request, response)    
    } catch (ex: Exception) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.writer.write("Authentication error: ${ex.message}}")
        }
    }
}