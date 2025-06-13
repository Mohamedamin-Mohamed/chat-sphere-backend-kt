package com.chatsphere.kotlin.service

import com.chatsphere.kotlin.dto.EmbeddingDTO
import com.chatsphere.kotlin.dto.VectorStoreDTO
import com.chatsphere.kotlin.exception.DocumentNotIndexedException
import org.opensearch.client.opensearch.OpenSearchClient
import org.opensearch.client.opensearch._types.Result
import org.opensearch.client.opensearch.core.IndexRequest
import org.opensearch.client.opensearch.core.SearchRequest
import org.opensearch.client.opensearch.core.SearchResponse
import org.opensearch.client.opensearch.core.search.Hit
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service


@Service
class OpenSearchService(
    @Value("\${OpenSearch.indexName}") val indexName: String,
    @Value("\${OpenSearch.searchField}") val searchField: String,
    private val client: OpenSearchClient,
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(OpenSearchService::class.java)
    }

    fun indexEmbeddings(embeddingDTO: EmbeddingDTO, embedding: FloatArray) {
        try {
            val vectorStoreDTO: VectorStoreDTO = getVectorStoreEquivalent(embeddingDTO, embedding)
            val indexRequest = IndexRequest
                .Builder<VectorStoreDTO>()
                .index(indexName)
                .document(vectorStoreDTO)
                .build()

            val indexResponse = client.index(indexRequest)
            if (indexResponse.result() != Result.Created && indexResponse.result() != Result.Updated) {
                throw DocumentNotIndexedException("An error occurred while indexing document.")
            }
        } catch (exp: Exception) {
            logger.error("Error occurred while indexing documents: $exp")
            throw DocumentNotIndexedException("An error occurred while indexing document.")
        }
    }

    fun getVectorStoreEquivalent(embeddingDTO: EmbeddingDTO, embeddingsVector: FloatArray): VectorStoreDTO {
        val vectorStoreDTO = VectorStoreDTO()
        vectorStoreDTO.embeddingDTO = embeddingDTO
        vectorStoreDTO.embeddings = embeddingsVector
        return vectorStoreDTO
    }

    fun searchEmbedding(embeddings: FloatArray): List<EmbeddingDTO> {
        logger.info("Searching from the vector store!!!")
        try {
            val searchRequest = SearchRequest
                .Builder()
                .index(indexName)
                .size(10)
                .query { q ->
                    q.knn {
                        it.field(searchField)
                            .vector(embeddings)
                    }
                }
                .build()

            val searchResponse: SearchResponse<VectorStoreDTO> =
                client.search(searchRequest, VectorStoreDTO::class.java)
            val embeddingDTOList: MutableList<EmbeddingDTO> = mutableListOf()

            for (vectorStoreDtoHit: Hit<VectorStoreDTO> in searchResponse.hits().hits()) {
                val vectorStoreDTO = vectorStoreDtoHit.source()
                if (vectorStoreDTO != null) {
                    val embeddingDTO = vectorStoreDTO.embeddingDTO
                    embeddingDTOList.add(embeddingDTO)
                }
            }
            return embeddingDTOList
        } catch (exp: Exception) {
            logger.error("OpenSearch search request failed $exp")
            return emptyList()
        }
    }

    fun searchResponse() {
        val searchRequest = SearchRequest
            .Builder()
            .index(indexName)
            .query {
                it.matchAll { m -> m }
            }
            .build()

        val searchResponse: SearchResponse<VectorStoreDTO> = client.search(searchRequest, VectorStoreDTO::class.java)

        for (hit: Hit<VectorStoreDTO> in searchResponse.hits().hits()) {
            val document = hit.source()

            if (document != null) {
                val embeddingDTO = document.embeddingDTO
                println("Document ID: ${hit.id()}")
                println("Question: ${embeddingDTO.question}")
                println("Answer: ${embeddingDTO.answer}")
                println("Embedding: ${document.embeddings}")
                println("Timestamp ${embeddingDTO.timestamp}")
            }
        }
    }
}