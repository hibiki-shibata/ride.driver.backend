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
import com.ride.driver.backend.services.JwtTokenService
import com.ride.driver.backend.services.AccessTokenData
import com.ride.driver.backend.services.CourierRoles
import com.ride.driver.backend.services.AdditionalAccessTokenClaims

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
        if (authHeader !== null && authHeader.startsWith("Bearer ")) {
            val jwtToken: String = authHeader.substringAfter("Bearer ")
            if (!jwtTokenService.isTokenValid(jwtToken)) throw Exception("Invalid or expired JWT token")
            val courierId: Int = jwtTokenService.extractCourierId(jwtToken)
            val courierName: String = jwtTokenService.extractCouriername(jwtToken)
            val courierRoles: List<CourierRoles> = jwtTokenService.extractRoles(jwtToken)
            println("JWT Token extracted: $jwtToken")
            val courierDetails: AccessTokenData = AccessTokenData(            
                additonalClaims = AdditionalAccessTokenClaims(courierId = courierId, roles = courierRoles),
                courierName = courierName
            )
            println("Courier details loaded: $courierDetails")
            val authenticationToken = UsernamePasswordAuthenticationToken(
                courierDetails, // principal
                null, // credentials
                courierRoles.map { SimpleGrantedAuthority(it.name) } // authorities
            )
            authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request) // Add web details like IP, session info in the context
            SecurityContextHolder.getContext().authentication = authenticationToken // It pass data so that business logic can use it
        }
        filterChain.doFilter(request, response)    
    } catch (ex: Exception) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.writer.write("{\"error\": \"jwtFilter error\", \"message\": \"${ex.message}\"}")
        }
    }
}