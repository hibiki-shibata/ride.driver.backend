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
import com.ride.driver.backend.auth.services.JwtTokenService
import com.ride.driver.backend.auth.domain.AccessTokenData
import com.ride.driver.backend.auth.domain.AccountRoles
import java.util.UUID

@Component 
class JwtFilter(
    private val jwtTokenService: JwtTokenService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
    try{
        SecurityContextHolder.getContext().authentication == null
        val authHeader: String? = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val jwtToken: String = authHeader.substringAfter("Bearer ")
            if (!jwtTokenService.isTokenValid(jwtToken)) throw Exception("Invalid or Expired JWT token")
            val accountDetails = AccessTokenData(                            
                    accountID = jwtTokenService.extractAccountId(jwtToken),
                    accountName = jwtTokenService.extractAccountName(jwtToken),
                    accountRoles = jwtTokenService.extractRoles(jwtToken)
            )
            val authenticationToken = UsernamePasswordAuthenticationToken(
                accountDetails, // principal
                null, // credentials
                accountDetails.accountRoles.map { SimpleGrantedAuthority(it.name) } // authorities
            )
            authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request) // Add web details like IP, session info in the context
            SecurityContextHolder.getContext().authentication = authenticationToken // It pass data so that business logic can use it
        }
        filterChain.doFilter(request, response) // Continue with the next filter in the chain
    } catch (ex: Exception) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.writer.write("{\"error\": \"jwtFilter error\", \"message\": \"${ex.message}\"}")
        }
    }
}