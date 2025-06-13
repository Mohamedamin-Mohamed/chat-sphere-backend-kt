package com.chatsphere.kotlin.dto

data class VectorStoreDTO(var embeddingDTO: EmbeddingDTO = EmbeddingDTO(), var embeddings: FloatArray = floatArrayOf()) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VectorStoreDTO

        if (embeddingDTO != other.embeddingDTO) return false
        if (!embeddings.contentEquals(other.embeddings)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = embeddingDTO.hashCode()
        result = 31 * result + embeddings.contentHashCode()
        return result
    }
}