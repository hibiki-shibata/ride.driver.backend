package com.ride.driver.backend.merchant.controller

import java.util.UUID
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.http.ResponseEntity
import com.ride.driver.backend.shared.auth.service.JwtTokenService
import com.ride.driver.backend.shared.auth.domain.AccessTokenData
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
import com.ride.driver.backend.shared.exceptions.BadRequestException
import com.ride.driver.backend.merchant.service.MerchantAuthService
import com.ride.driver.backend.merchant.dto.MerchantSignInDTO
import com.ride.driver.backend.merchant.dto.MerchantLoginDTO
import com.ride.driver.backend.merchant.model.MerchantProfile

@RestController
@RequestMapping("/api/v1/merchants")
class MerchantAuthController(
    private val merchantAuthService: MerchantAuthService,
    private val jwtTokenService: JwtTokenService,
) {
    private fun isPasswordValid(inputPassword: String, storedPasswordHash: String): Boolean {
        return inputPassword.hashCode().toString() == storedPasswordHash        
    } 
    
    @PostMapping("/auth/signup")
    fun merchantSignup(@RequestBody @Valid req: MerchantSignInDTO): ResponseEntity<JwtTokensDTO> {
        val savedMerchant: MerchantProfile = merchantAuthService.registerNewMerchant(
            name = req.name,
            phoneNumber = req.phoneNumber,
            password = req.password,
            merchantAddress = req.merchantAddress,
            merchantAddressCoordinate = req.merchantAddressCoordinate
        )
        val accessToken: String = jwtTokenService.generateAccessToken(
            AccessTokenData(
                accountID = savedMerchant.id ?: throw Exception("Merchant ID is null"),
                accountName = savedMerchant.name,
                accountRoles = listOf(AccountRoles.BASE_ROLE)
            )
        )
        val refreshToken = jwtTokenService.generateRefreshToken(savedMerchant.id)
        return ResponseEntity.ok(JwtTokensDTO(accessToken = accessToken, refreshToken = refreshToken))        
    }

    @PostMapping("/auth/login")
    fun merchantLogin(@RequestBody @Valid req: MerchantLoginDTO): ResponseEntity<JwtTokensDTO> {
        val savedMerchant: MerchantProfile = merchantAuthService.getMerchantProfileByPhoneNumberAndValidatePassword(
            phoneNumber = req.phoneNumber,
            password = req.password
        )
        val accessToken: String = jwtTokenService.generateAccessToken(
            AccessTokenData(
                accountID = savedMerchant.id ?: throw Exception("Merchant ID is null"),
                accountName = savedMerchant.name,
                accountRoles = listOf(AccountRoles.BASE_ROLE)
            )
        )
        val refreshToken: String = jwtTokenService.generateRefreshToken(savedMerchant.id)
        return ResponseEntity.ok(JwtTokensDTO(accessToken = accessToken, refreshToken = refreshToken))
    }
}