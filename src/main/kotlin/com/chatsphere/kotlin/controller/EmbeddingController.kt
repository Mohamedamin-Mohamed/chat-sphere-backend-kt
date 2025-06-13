package com.chatsphere.kotlin.controller

import com.chatsphere.kotlin.dto.EmbeddingDTO
import com.chatsphere.kotlin.dto.EmbeddingRequestDTO
import com.chatsphere.kotlin.service.EmbeddingService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/embeddings")
class EmbeddingController(
    private val embeddingService: EmbeddingService
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(EmbeddingController::class.java)
    }

    @PostMapping
    fun createEmbedding(@RequestBody embeddingDTO: EmbeddingDTO): ResponseEntity<Any> {
        logger.info("Creating embedding for question ${embeddingDTO.question}")
        embeddingService.storeEmbeddings(embeddingDTO)

        return ResponseEntity
            .ok()
            .build()
    }

    @PostMapping("/search")
    fun searchEmbeddings(@RequestBody embeddingRequestDTO: EmbeddingRequestDTO): ResponseEntity<Any> {
        logger.info("Searching embedding for input ${embeddingRequestDTO.question}")
        val embeddingDTOList = embeddingService.searchEmbedding(embeddingRequestDTO)
        logger.info("Size of returned list is ${embeddingDTOList.size}")
        return ResponseEntity
            .ok()
            .body(embeddingDTOList)
    }
}