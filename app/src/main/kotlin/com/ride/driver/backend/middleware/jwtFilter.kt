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
import com.ride.driver.backend.service.JwtTokenService

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
        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val jwtToken: String = authHeader.substringAfter("Bearer ")

            if (SecurityContextHolder.getContext().authentication == null && jwtTokenService.isTokenValid(jwtToken)) {
                val username = jwtTokenService.extractUsername(jwtToken)
                val userRoles = jwtTokenService.extractRoles(jwtToken)
                val userDetails = jwtTokenService.extractUserDetails(jwtToken)

                val authenticationToken = UsernamePasswordAuthenticationToken(
                    userDetails, // Principal
                    null, // Credentials
                    userRoles.map { role -> SimpleGrantedAuthority(role) } // Authorities
                )
                authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request) // Add web details like IP, session info in the context
                SecurityContextHolder.getContext().authentication = authenticationToken // It pass data so that  business logic can use it            
            }
        }
        filterChain.doFilter(request, response)
    } catch (ex: Exception) {
        response.writer.write(
                """{"error": "Filter Authorization error: 
                |${ex.message ?: "unknown error"}"}""".trimMargin()
            )   
        }
    }
}