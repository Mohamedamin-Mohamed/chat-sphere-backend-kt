package com.chatsphere.kotlin.controller

import com.chatsphere.kotlin.dto.ApiResponseDTO
import com.chatsphere.kotlin.dto.SendCodeResultDTO
import com.chatsphere.kotlin.dto.TwilioSendCodeDTO
import com.chatsphere.kotlin.dto.TwilioVerifyCodeDTO
import com.chatsphere.kotlin.service.TwilioService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/twilio/code")
class TwilioVerificationController(
    private val twilioService: TwilioService
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(TwilioVerificationController::class.java)
    }

    @PostMapping("/send")
    fun codeSending(@RequestBody sendCodeDTO: TwilioSendCodeDTO): ResponseEntity<ApiResponseDTO> {
        logger.info("Sending code for phone number: ${sendCodeDTO.phoneNumber}")

        val result: SendCodeResultDTO = twilioService.sendCode(sendCodeDTO)
        if (result.success) {
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO(true, result.message))
        }
        return ResponseEntity
            .internalServerError()
            .body(ApiResponseDTO(false, result.message))
    }

    @PostMapping("/verify")
    fun codeVerification(@RequestBody verifyPinDTO: TwilioVerifyCodeDTO): ResponseEntity<ApiResponseDTO> {
        logger.info("Verifying pin for ${verifyPinDTO.email}")

        val codeVerified = twilioService.verifyCode(verifyPinDTO)
        if (codeVerified) {
            logger.info("Code verified successfully")
            return ResponseEntity
                .ok()
                .body(ApiResponseDTO(true, "Pin verified"))
        }

        logger.warn("Code verification failed")
        return ResponseEntity
            .ok()
            .body(ApiResponseDTO(false, "Invalid code"))
    }

}