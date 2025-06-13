package com.chatsphere.kotlin.service

import com.chatsphere.kotlin.config.TwilioConfig
import com.chatsphere.kotlin.dto.SendCodeResultDTO
import com.chatsphere.kotlin.dto.TwilioSendCodeDTO
import com.chatsphere.kotlin.dto.TwilioVerifyCodeDTO
import com.chatsphere.kotlin.dto.UpdateProfileDTO
import com.twilio.rest.lookups.v1.PhoneNumberFetcher
import com.twilio.rest.verify.v2.service.VerificationCheck
import com.twilio.rest.verify.v2.service.VerificationCreator
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TwilioService(
    @Value("\${twilio.statusPending}") private val statusPending: String,
    @Value("\${twilio.statusApproved}") private val statusApproved: String,
    @Value("\${twilio.verificationSID}") private val serviceID: String,
    private val twilioConfig: TwilioConfig,
    private val userService: UserService
) {
    companion object {
        val logger = LoggerFactory.getLogger(TwilioService::class.java)
    }

    fun sendCode(sendCodeDTO: TwilioSendCodeDTO): SendCodeResultDTO {
        try {
            twilioConfig.connect()

            val phoneNum = sendCodeDTO.phoneNumber
            val number = PhoneNumberFetcher(com.twilio.type.PhoneNumber(phoneNum).toString()).fetch()

            if (number == null) {
                val verification = VerificationCreator(serviceID, phoneNum, "sms").create()
                val isPending = statusPending == verification.status

                return SendCodeResultDTO(isPending, "Code sent successfully.")
            } else {
                return SendCodeResultDTO(false, "Invalid phone number.")
            }
        } catch (exp: Exception) {
            logger.error("Failed to send verification code to ${sendCodeDTO.phoneNumber}: ${exp.message}")
            return SendCodeResultDTO(false, exp.message.toString())
        }
    }

    fun verifyCode(verifyPinDTO: TwilioVerifyCodeDTO): Boolean {
        try {
            twilioConfig.connect()

            val verificationCheck = VerificationCheck
                .creator(serviceID)
                .setTo(verifyPinDTO.phoneNumber)
                .create()

            val isApproved = statusApproved == verificationCheck.status
            if (isApproved) {
                val updateProfileDto = UpdateProfileDTO()
                updateProfileDto.phoneNumber = verifyPinDTO.phoneNumber
                userService.updateProfile(updateProfileDto, null)
                return true
            }
            logger.warn("Verification status for ${verifyPinDTO.phoneNumber} is ${verificationCheck.status}")
            return false
        } catch (exp: Exception) {
            logger.info("Verification failed for ${verifyPinDTO.phoneNumber}: ${exp.message}, $exp")
            return false
        }
    }
}