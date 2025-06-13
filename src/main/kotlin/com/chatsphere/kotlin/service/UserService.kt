package com.chatsphere.kotlin.service

import com.chatsphere.kotlin.dto.*
import com.chatsphere.kotlin.exception.EmailAlreadyExistsException
import com.chatsphere.kotlin.exception.EmailNotFoundException
import com.chatsphere.kotlin.exception.IncorrectPasswordException
import com.chatsphere.kotlin.exception.OAuthSignInRequiredException
import com.chatsphere.kotlin.mapper.ModelMapper
import com.chatsphere.kotlin.mapper.UserSearchDTOMapper
import com.chatsphere.kotlin.model.User
import com.chatsphere.kotlin.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import org.springframework.web.multipart.MultipartFile

@Service
@Validated
class UserService(
    private val userRepository: UserRepository,
    private val passwordService: PasswordService,
    private val modelMapper: ModelMapper,
    private val filesUploadService: FilesUploadService,
    private val userSearchDTOMapper: UserSearchDTOMapper
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(UserService::class.java)
    }

    @Transactional
    fun signUpWithEmail(signUpRequest: SignUpRequestDTO): UserDTO {
        if (findByEmail(signUpRequest.email) != null) {
            throw EmailAlreadyExistsException("Email already exists")
        }

        val user = modelMapper.map(signUpRequest)
        userRepository.save(user)

        return modelMapper.map(user)
    }

    @Transactional
    fun signUpWithAuth(oAuthSignUpRequest: OAuthSignUpRequestDTO): UserDTO {
        val existingUser: User? = findByEmail(oAuthSignUpRequest.email)
        existingUser?.let {
            if (it.oauthProvider == null || it.oauthProvider != oAuthSignUpRequest.oauthProvider) {
                throw EmailAlreadyExistsException("Email already registered")
            }
            return modelMapper.map(it)
        } ?: run {
            val user = modelMapper.map(oAuthSignUpRequest)
            userRepository.save(user)

            return modelMapper.map(user)
        }
    }

    @Transactional
    fun signInWithEmail(signInDto: SignInDTO): UserDTO {
        val user: User = findByEmail(signInDto.email) ?: throw EmailNotFoundException("Email not found")
        logger.info("Oauth provider is ${user.oauthProvider} and oauth id is ${user.oauthId}")
        if (user.oauthProvider != null && user.oauthId != null) {
            throw OAuthSignInRequiredException("Account exists sign in with ${user.oauthProvider} provider")
        }

        if (!passwordService.verifyPassword(signInDto.password, user.password)) {
            throw IncorrectPasswordException("Incorrect password")
        }

        return modelMapper.map(user)
    }

    @Transactional
    fun findByEmail(email: String): User? = userRepository.findByEmail(email)

    @Transactional
    fun findRawUserEmail(email: String): User =
        findByEmail(email) ?: throw EmailNotFoundException("User email not found")

    fun findByOauthId(oauthId: String): User = userRepository.findByOauthId(oauthId)

    fun existsByEmail(email: String): Boolean = userRepository.existsByEmail(email)

    @Transactional
    fun resetPassword(genericObject: Any): Boolean {
        try {
            val mapper = ObjectMapper()
            val dataMap = mapper.convertValue(genericObject, Map::class.java)
            val convertedObject: Any = when {
                dataMap.containsKey("email") && dataMap.containsKey("password") -> mapper.convertValue(
                    genericObject,
                    PasswordResetDTO::class.java
                )

                dataMap.containsKey("email") && dataMap.containsKey("currentPassword") && dataMap.containsKey("newPassword") -> mapper.convertValue(
                    genericObject,
                    UpdatedPasswordDTO::class.java
                )

                else -> return false
            }

            (convertedObject as? PasswordResetDTO)?.let {
                val user = findByEmail(it.email) ?: return false

                val rawPassword = it.password
                val hashedPassword = passwordService.hashPassword(rawPassword)

                user.passwd = hashedPassword
                userRepository.save(user)

                return true
            }

            (convertedObject as? UpdatedPasswordDTO)?.let {
                val user = findByEmail(it.email) ?: return false

                val isPasswordCorrect = passwordService.verifyPassword(it.currentPassword, user.password)
                if (!isPasswordCorrect) return false

                val newHashedPassword = passwordService.hashPassword(it.newPassword)
                user.passwd = newHashedPassword
                userRepository.save(user)

                return true
            }
            return false
        } catch (exp: Exception) {
            logger.error("Error processing password reset/update $exp")
        }
        return false
    }

    @Transactional
    fun deleteUserById(email: String) {
        val user = findByEmail(email)
        user?.let { userRepository.delete(user) }
    }

    @Transactional
    fun updateProfile(@Valid updateProfileDTO: UpdateProfileDTO, multipartFile: MultipartFile?): UserDTO? {
        val user: User = findByEmail(updateProfileDTO.email) ?: throw EmailNotFoundException("Email not found")

        //check if email is being changed
        val currentEmail = user.email
        val newEmail = updateProfileDTO.newEmail

        if (newEmail.isNotEmpty() && currentEmail != newEmail) {
            findByEmail(newEmail)?.let {
                if (it.id != user.id) throw EmailAlreadyExistsException("Email already in use.")
            }
            user.email = newEmail
        }
        if (updateProfileDTO.name.isNotEmpty()) {
            user.name = updateProfileDTO.name
        }

        if (updateProfileDTO.bio.isNotEmpty()) {
            user.bio = updateProfileDTO.bio
        }

        val publicFileUrl =
            multipartFile?.let { filesUploadService.uploadFileToS3Bucket(it) }?.takeIf { it.isNotEmpty() }
        publicFileUrl?.let { user.picture = it }

        if (updateProfileDTO.phoneNumber.isNotEmpty()) {
            user.phoneNumber = updateProfileDTO.phoneNumber
        }

        userRepository.save(user)
        return modelMapper.map(user)
    }

    fun searchUsers(searchRequest: SearchRequest): List<UserSearchDTO> {
        return try {
            val users = userRepository.searchUsersByNameOrEmail(searchRequest.query, searchRequest.requesterEmail)
            users.map { userSearchDTOMapper.apply(it, searchRequest.requesterEmail) }
        } catch (exp: Exception) {
            logger.error("Something went wrong: $exp")
            emptyList()
        }
    }

    @Transactional
    fun getUserStats(email: String): UserStatsDTO {
        val user = findByEmail(email) ?: throw EmailNotFoundException("User email was not found")

        val followingList = user.followers
        val followerList = user.followings
        return modelMapper.map(followerList, followingList)
    }
}