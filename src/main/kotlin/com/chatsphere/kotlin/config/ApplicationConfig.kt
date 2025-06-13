package com.chatsphere.kotlin.config

import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.apache.http.ssl.SSLContextBuilder
import org.opensearch.client.RestClient
import org.opensearch.client.json.jackson.JacksonJsonpMapper
import org.opensearch.client.opensearch.OpenSearchClient
import org.opensearch.client.transport.rest_client.RestClientTransport
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

import javax.net.ssl.SSLContext
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

@Configuration
class ApplicationConfig(
    @Value("\${OpenSearch.url}") val url: String,
    @Value("\${OpenSearch.username}") val userName: String,
    @Value("\${OpenSearch.password}") val password: String,
    @Value("\${OpenSearch.port}") val port: Int,
    @Value("\${OpenSearch.protocol}") val protocol: String,
    @Value("\${AWS.accessKeyId}") val accessKeyId: String,
    @Value("\${AWS.secretAccessKey}") val secretAccessKey: String,
    @Value("\${AWS.region}") val region: String
) {
    @Bean
    fun awsCredentialsProvider(): AwsCredentialsProvider {
        return AwsCredentialsProvider {
            AwsBasicCredentials.create(accessKeyId, secretAccessKey)
        }
    }

    @Bean
    fun s3Client(): S3Client {
        return S3Client
            .builder()
            .region(Region.of(region))
            .credentialsProvider(awsCredentialsProvider())
            .build()
    }

    @Bean
    fun openSearchCredentialsProvider(): CredentialsProvider {
        val basicCredentialsProvider = BasicCredentialsProvider()
        basicCredentialsProvider.setCredentials(AuthScope.ANY, UsernamePasswordCredentials(userName, password))
        return basicCredentialsProvider
    }

    @Bean
    fun httpHost(): HttpHost {
        return HttpHost(url, port, protocol)
    }

    @Bean
    fun sslContext(): SSLContext? {
        return SSLContextBuilder.create().build()
    }

    @Bean
    fun restClient(): RestClient {
        return RestClient.builder(httpHost())
            .setHttpClientConfigCallback { httpClientBuilder: HttpAsyncClientBuilder ->
                try {
                    httpClientBuilder
                        .setDefaultCredentialsProvider(openSearchCredentialsProvider())
                        .setSSLContext(sslContext())
                } catch (exp: NoSuchAlgorithmException) {
                    throw RuntimeException(exp)
                } catch (exp: KeyManagementException) {
                    throw RuntimeException(exp)
                }
            }
            .build()
    }



    @Bean
    fun openSearchTransport(): RestClientTransport {
        return RestClientTransport(restClient(), JacksonJsonpMapper())
    }

    @Bean
    fun openSearchClient(): OpenSearchClient {
        return OpenSearchClient(openSearchTransport())
    }
}