// package com.ride.driver.backend.controller

// @RestController
// @RequestMapping("/api/v1/auth")
// class AuthController(
//     private val authenticationService: AuthenticationService
// ) {
//     @PostMapping
//     fun authenticate(
//       @RequestBody authRequest: AuthenticationRequest
//     ): AuthenticationResponse =
//         authenticationService.authentication(authRequest)

//     @PostMapping("/refresh")
//     fun refreshAccessToken(
//       @RequestBody request: RefreshTokenRequest
//     ): TokenResponse = TokenResponse(token = authenticationService.refreshAccessToken(request.token))
// }