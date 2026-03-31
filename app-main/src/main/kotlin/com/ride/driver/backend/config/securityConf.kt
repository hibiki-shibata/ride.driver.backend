package com.ride.driver.backend.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import com.ride.driver.backend.middleware.JwtFilter
import com.ride.driver.backend.shared.auth.domain.AccountRoles

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtFilter: JwtFilter): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors {}
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers("/api/v1/*/auth/**").permitAll()
                it.requestMatchers("/api/v1/logistics/**").hasAnyAuthority(AccountRoles.ADMIN_ROLE.name)
                it.requestMatchers("/api/v1/couriers/**").hasAnyAuthority(AccountRoles.BASE_COURIER_ROLE.name, AccountRoles.ADMIN_ROLE.name)
                it.requestMatchers("/api/v1/consumers/**").hasAnyAuthority(AccountRoles.BASE_CONSUMER_ROLE.name, AccountRoles.ADMIN_ROLE.name)
                it.requestMatchers("/api/v1/merchants/**").hasAnyAuthority(AccountRoles.BASE_MERCHANT_ROLE.name, AccountRoles.ADMIN_ROLE.name)
                it.anyRequest().authenticated()
            }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    // CORS configuration to allow requests from frontend
    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:5173")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder(10)
    
}