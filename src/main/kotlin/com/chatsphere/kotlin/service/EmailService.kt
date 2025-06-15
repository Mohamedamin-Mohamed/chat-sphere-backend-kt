package com.chatsphere.kotlin.service

import com.chatsphere.kotlin.config.MailjetConfig
import com.chatsphere.kotlin.config.properties.FileNamesProperties
import com.chatsphere.kotlin.config.properties.MailjetProperties
import com.chatsphere.kotlin.dto.MailjetDTO
import com.chatsphere.kotlin.exception.EmptyVerificationCodeException
import com.mailjet.client.MailjetRequest
import com.mailjet.client.resource.Emailv31
import org.json.JSONArray
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader

@Service
class EmailService(
    private val mailjetConfig: MailjetConfig,
    private val mailjetProperties: MailjetProperties,
    private val fileNamesProperties: FileNamesProperties,
) {
    val logger: Logger = LoggerFactory.getLogger(EmailService::class.java)

    fun sendVerificationEmail(recipientEmail: String, code: String): Boolean {
        logger.info("Sending verification code $code for $recipientEmail")
        if (code.isEmpty()) throw EmptyVerificationCodeException("Verification code need to be specified")

        val template = getEmailTemplate(fileNamesProperties.verificationCode)
        val templateLines: List<String> = template.split("\\r?\\n")

        val subject = templateLines[0].replace("Subject: ", "")
        val bodyContent = templateLines.subList(1, templateLines.size).joinToString("\n")

        bodyContent.replace("{verification_code}", code)

        val mailjetDTO = MailjetDTO()
        mailjetDTO.recipientEmail = recipientEmail
        mailjetDTO.subject = subject
        mailjetDTO.bodyContent = bodyContent

        return setMailjetConfig(mailjetDTO)
    }

    fun setMailjetConfig(mailjetDTO: MailjetDTO): Boolean {
        try {
            //convert to HTML with proper formatting
            val htmlContent = mailjetDTO.bodyContent.replace("\n", "<br>")

            val client = mailjetConfig.connect()
            val request = MailjetRequest(Emailv31.resource)
                .property(
                    Emailv31.MESSAGES, JSONArray().put(
                        JSONObject()
                            .put(
                                Emailv31.Message.FROM, JSONObject()
                                    .put("Email", mailjetProperties.senderEmail)
                                    .put("Name", mailjetProperties.senderName)
                            )
                            .put(
                                Emailv31.Message.TO, JSONArray().put(
                                    JSONObject().put("Email", mailjetDTO.recipientEmail)
                                )
                            )
                            .put(Emailv31.Message.SUBJECT, mailjetDTO.subject)
                            .put(Emailv31.Message.TEXTPART, mailjetDTO.bodyContent)
                            .put(Emailv31.Message.HTMLPART, htmlContent)
                    )
                )
            val response = client.post(request)
            val responseStatus: Boolean = when {
                response.status == 200 -> true.also { logger.info("Email sent successfully ${response.data}") }
                else -> false.also { logger.error("Error sending email ${response.status}") }
            }
            return responseStatus
        } catch (exp: Exception) {
            logger.error("Error reading content ${exp.message}")
            throw RuntimeException("Error sending email", exp)
        }
    }

    fun getEmailTemplate(fileName: String): String {
        // access the template file from the resources folder
        val inputSteam = javaClass.classLoader.getResourceAsStream("EmailTemplates/$fileName")
            ?: throw IllegalArgumentException("Template file not found")

        try {
            val reader = BufferedReader(InputStreamReader(inputSteam))
            val templateBuilder = StringBuilder()
            var line: String? = reader.readLine()

            while (line != null) {
                templateBuilder.append(line).append("\n")
                line = reader.readLine()
            }
            return templateBuilder.toString()
        } catch (exp: Exception) {
            throw RuntimeException("Error reading email template $exp")
        }
    }

}