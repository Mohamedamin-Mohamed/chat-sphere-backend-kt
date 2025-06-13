package com.chatsphere.kotlin.util

import com.fasterxml.jackson.databind.ObjectMapper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class HttpResponseUtil {
    companion object {
        fun httpResponse(apiKey: String, uri: String, requestBody: MutableMap<String, Any>): HttpResponse<String> {
            val httpClient = HttpClient.newHttpClient()
            val objectMapper = ObjectMapper()
            val jsonBody = objectMapper.writeValueAsString(requestBody)

            val httpRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer $apiKey")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build()

            return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())
        }
    }
}