package com.ride.driver.backend.middlewares

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.stereotype.Component;
// import javax.servlet.*;
// import java.io.IOException;


// @Component
// open class LogFilter : Filter {

//     private val logger: Logger = LoggerFactory.getLogger(LogFilter::class.java)

//     @Throws(IOException::class, ServletException::class)
//     override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
//         logger.info("Hello from: " + request.localAddr)
//         chain.doFilter(request, response)
//     }

//     override fun init(filterConfig: FilterConfig?) {
//         // Initialization logic if needed
//     }

//     override fun destroy() {
//         // Cleanup logic if needed
//     }
// }