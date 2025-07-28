// Doc https://spring.io/guides/gs/rest-service-cors#global-cors-configuration
package com.ride.driver.backend.middlewares

import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurator
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.springframework.web.servlet.config.annotation.WebMvcConfiguration

// cors global configuration / because it doesnt use Spring security, I use WebMvcConfigurator not URLBasedCorsConfigurer or SecurityFilterChain(working before selvlet)
open class CorsConf: WebMvcConfigurator{
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000", "https://example.com")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
    }
}