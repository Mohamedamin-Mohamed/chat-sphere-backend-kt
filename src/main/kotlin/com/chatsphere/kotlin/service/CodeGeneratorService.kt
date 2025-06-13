package com.chatsphere.kotlin.service

import org.springframework.stereotype.Service
import java.security.SecureRandom

@Service
class CodeGeneratorService {
    fun generateCode(): String {
        val secureRandom = SecureRandom()
        val code = 100000 + secureRandom.nextInt(900000)
        return code.toString()
    }
}