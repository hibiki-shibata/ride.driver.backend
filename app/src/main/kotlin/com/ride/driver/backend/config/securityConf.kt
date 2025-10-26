// package com.ride.driver.backend.config

// import org.springframework.security.config.annotation.web.invoke

// @Bean
// open fun filterChain(http: HttpSecurity): SecurityFilterChain {
//     http {
//         authorizeHttpRequests {
//             authorize(anyRequest, authenticated)
//         }
//         formLogin { }
//         httpBasic { }
//     }
//     return http.build()
// }