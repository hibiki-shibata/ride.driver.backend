package com.ride.driver.backend.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import com.ride.driver.backend.middleware.JwtFilter
import com.ride.driver.backend.services.CourierRoles

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtFilter: JwtFilter): SecurityFilterChain {
        http
            .csrf { it.disable() }
            // .cors { it.disabled() }
            .cors {}
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers("/api/v1/auth/**").permitAll()
                it.requestMatchers("/api/v1/couriers/**").hasAnyAuthority(CourierRoles.BASE_ROLE.name, CourierRoles.ADMIN_ROLE.name)
                it.anyRequest().authenticated()
            }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}