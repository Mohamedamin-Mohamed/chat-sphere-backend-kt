package com.chatsphere.kotlin.service

import com.chatsphere.kotlin.config.properties.OpenAIProperties
import com.chatsphere.kotlin.dto.EmbeddingDTO
import com.chatsphere.kotlin.dto.EmbeddingRequestDTO
import com.chatsphere.kotlin.exception.EmbeddingNotCreatedException
import com.chatsphere.kotlin.util.HttpResponseUtil
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.http.HttpResponse

@Service
class EmbeddingService(
    private val openAIProperties: OpenAIProperties,
    private val openSearchService: OpenSearchService,
) {
    val logger: Logger = LoggerFactory.getLogger(EmbeddingService::class.java)

    fun createEmbedding(input: String): HttpResponse<String> {
        try {
            val requestBody: MutableMap<String, Any> = hashMapOf()
            requestBody["model"] = openAIProperties.embeddingModel
            requestBody["input"] = input


            val httpResponse =
                HttpResponseUtil.httpResponse(openAIProperties.apiKey, openAIProperties.embeddingURL, requestBody)
            if (httpResponse.statusCode() != 200) {
                throw EmbeddingNotCreatedException("Error occurred when creating embedding")
            }
            return httpResponse
        } catch (exp: Exception) {
            logger.error("Something went wrong $exp")
            throw EmbeddingNotCreatedException("Error occurred when creating embedding")
        }
    }

    fun searchEmbedding(embeddingRequestDTO: EmbeddingRequestDTO): List<EmbeddingDTO> {
        val httpResponse = createEmbedding(embeddingRequestDTO.question)
        val embedding: FloatArray = retrieveEmbedding(httpResponse)
        return openSearchService.searchEmbedding(embedding)
    }

    fun storeEmbeddings(embeddingDTO: EmbeddingDTO) {
        val httpResponse = createEmbedding(embeddingDTO.question)
        val embeddings = retrieveEmbedding(httpResponse)

        openSearchService.indexEmbeddings(embeddingDTO, embeddings)
    }

    fun retrieveEmbedding(httpResponse: HttpResponse<String>): FloatArray {
        val objectMapper = ObjectMapper()

        try {
            val rootNode = objectMapper.readTree(httpResponse.body())
            val node = rootNode.path("data")[0].path("embedding")

            return FloatArray(node.size()) { i -> node[i].asDouble().toFloat() }
        } catch (exp: java.lang.Exception) {
            logger.error("Couldn't retrieve embeddings from response body $exp")
            return floatArrayOf()
        }
    }

}