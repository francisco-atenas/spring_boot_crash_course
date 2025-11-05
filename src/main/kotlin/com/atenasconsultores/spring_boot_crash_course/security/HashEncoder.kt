package com.atenasconsultores.spring_boot_crash_course.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class HashEncoder {
    private val bycrypt = BCryptPasswordEncoder()

    fun encode(raw: String): String = bycrypt.encode(raw)


    fun matches(password: String, hashed: String): Boolean = bycrypt.matches(password, hashed)

}