package com.atenasconsultores.spring_boot_crash_course.security

import com.atenasconsultores.spring_boot_crash_course.database.model.RefreshToken
import com.atenasconsultores.spring_boot_crash_course.database.model.User
import com.atenasconsultores.spring_boot_crash_course.database.repository.RefreshTokenRepository
import com.atenasconsultores.spring_boot_crash_course.database.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    data class TokenPair(
        val accessToken: String,
        val refreshToken: String
    )

    fun register(email: String, password: String): User {
        return userRepository.save(
            User(
                email = email,
                hashedPassword = hashEncoder.encode(password)
            )

        )
    }

    fun login(email: String, password: String): TokenPair {
        val user = userRepository.findByEmail(email)
            ?: throw BadCredentialsException("Invalid credentials.")
        if (!hashEncoder.matches(password, user.hashedPassword)) {
            throw BadCredentialsException("Invalid credentials.")

        }
        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        storeRefreshToken(user.id, newRefreshToken)

        return TokenPair(accessToken = newAccessToken, refreshToken = newRefreshToken)

    }

    private fun storeRefreshToken(userId: ObjectId, rawRefreshToken:String){
val hashed = hashToken(rawRefreshToken)
        val expityMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expityMs)

        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                hashedToken = hashed,
                expiresAt = expiresAt
            )
        )
    }
    private fun hashToken(token:String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}