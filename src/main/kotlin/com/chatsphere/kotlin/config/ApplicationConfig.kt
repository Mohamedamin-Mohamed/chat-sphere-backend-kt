package com.chatsphere.kotlin.config

import com.chatsphere.kotlin.config.properties.AWSProperties
import com.chatsphere.kotlin.config.properties.OpenSearchProperties
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
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import javax.net.ssl.SSLContext

@Configuration
class ApplicationConfig(
    private val openSearchProperties: OpenSearchProperties,
    private val awsProperties: AWSProperties,
) {
    @Bean
    fun awsCredentialsProvider(): AwsCredentialsProvider {
        return AwsCredentialsProvider {
            AwsBasicCredentials.create(awsProperties.accessKeyId, awsProperties.secretAccessKey)
        }
    }

    @Bean
    fun s3Client(): S3Client {
        return S3Client
            .builder()
            .region(Region.of(awsProperties.region))
            .credentialsProvider(awsCredentialsProvider())
            .build()
    }

    @Bean
    fun openSearchCredentialsProvider(): CredentialsProvider {
        val basicCredentialsProvider = BasicCredentialsProvider()
        basicCredentialsProvider.setCredentials(
            AuthScope.ANY,
            UsernamePasswordCredentials(openSearchProperties.userName, openSearchProperties.password)
        )
        return basicCredentialsProvider
    }

    @Bean
    fun httpHost(): HttpHost {
        return HttpHost(openSearchProperties.url, openSearchProperties.port, openSearchProperties.protocol)
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