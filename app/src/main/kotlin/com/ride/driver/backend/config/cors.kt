// Doc https://spring.io/guides/gs/rest-service-cors#global-cors-configuration
package com.ride.driver.backend.config

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.context.annotation.Configuration

// cors global configuration / because it doesnt use Spring security, I use WebMvcConfigurator not URLBasedCorsConfigurer or SecurityFilterChain(working before selvlet)
@Configuration
open class CorsConf: WebMvcConfigurer{
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000", "https://example.com")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
    }
}