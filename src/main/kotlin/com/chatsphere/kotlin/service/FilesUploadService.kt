package com.chatsphere.kotlin.service

import com.chatsphere.kotlin.config.properties.AWSProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@Service
class FilesUploadService(
    private val s3Client: S3Client,
    private val awsProperties: AWSProperties
) {
    private val logger: Logger = LoggerFactory.getLogger(FilesUploadService::class.java)

    fun uploadFileToS3Bucket(multipartFile: MultipartFile): String {
        logger.info("Uploading file to s3 bucket")
        if (multipartFile.isEmpty) return ""

        return try {
            val fileName = multipartFile.originalFilename
            val s3Key = awsProperties.s3BucketKeyName + fileName

            val putObjectRequest = PutObjectRequest
                .builder()
                .bucket(awsProperties.s3BucketName)
                .key(s3Key)
                .contentType(multipartFile.contentType)
                .build()

            val putObjectResponse = s3Client.putObject(
                putObjectRequest,
                /* avoids memory issues as it streams the file directly without
                           loading the entire content into memory. */
                RequestBody.fromInputStream(multipartFile.inputStream, multipartFile.size)
            )

            if (putObjectResponse.sdkHttpResponse().isSuccessful) {
                getPublicUrl(s3Key)
            }
            ""
        } catch (exp: Exception) {
            logger.error("Error uploading file to s3 $exp")
            ""
        }
    }

    private fun getPublicUrl(s3Key: String): String {
        return "https://${awsProperties.s3BucketName}.s3.${awsProperties.region}.amazon.aws.com/$s3Key"
    }
}