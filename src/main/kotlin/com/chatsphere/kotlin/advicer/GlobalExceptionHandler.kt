package com.chatsphere.kotlin.advicer

import com.chatsphere.kotlin.exception.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleRuntimeException(exp: RuntimeException): ResponseEntity<String> {
        return ResponseEntity
            .internalServerError()
            .body("Internal server error occurred: ${exp.message}")
    }

    @ExceptionHandler(EmailAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleEmailAlreadyExistsException(exp: EmailAlreadyExistsException): ResponseEntity<String> {
        return ResponseEntity
            .badRequest()
            .body(exp.message)
    }

    @ExceptionHandler(EmailNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleEmailNotFoundException(exp: EmailNotFoundException): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(exp.message)
    }

    @ExceptionHandler(IncorrectPasswordException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleIncorrectPasswordException(exp: IncorrectPasswordException): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(exp.message)
    }

    @ExceptionHandler(OAuthSignInRequiredException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleOauthSignInException(exp: OAuthSignInRequiredException): ResponseEntity<String> {
        return ResponseEntity
            .badRequest()
            .body(exp.message)
    }

    @ExceptionHandler(EmptyVerificationCodeException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleEmptyVerificationCodeException(exp: EmptyVerificationCodeException): ResponseEntity<String> {
        return ResponseEntity
            .badRequest()
            .body(exp.message)
    }

    @ExceptionHandler(DocumentNotIndexedException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleDocumentNotIndexedException(exp: DocumentNotIndexedException): ResponseEntity<String> {
        return ResponseEntity
            .internalServerError()
            .body(exp.message)
    }


}