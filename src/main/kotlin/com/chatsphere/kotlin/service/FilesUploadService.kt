package com.chatsphere.kotlin.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@Service
class FilesUploadService(
    val s3Client: S3Client,
    @Value("\${AWS.s3BucketName}") val bucketName: String,
    @Value("\${AWS.s3BucketKeyName}") val keyName: String,
    @Value("\${AWS.region}") val region: String
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(FilesUploadService::class.java)
    }

    fun uploadFileToS3Bucket(multipartFile: MultipartFile): String {
        logger.info("Uploading file to s3 bucket")
        if (multipartFile.isEmpty) return ""

        return try {
            val fileName = multipartFile.originalFilename
            val s3Key = keyName + fileName

            val putObjectRequest = PutObjectRequest
                .builder()
                .bucket(bucketName)
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
        return String.format("https://$bucketName.s3.$region.amazon.aws.com/$s3Key")
    }
}