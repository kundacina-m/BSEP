package com.mkundacina.pki.db

import com.mkundacina.pki.model.User
import com.mkundacina.pki.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class DbInit(private val userRepository: UserRepository, private val passwordEncoder: PasswordEncoder) : CommandLineRunner {
    override fun run(vararg args: String) {
        // Delete all
        userRepository.deleteAll()

        // Crete users
        val dan = User("user", passwordEncoder.encode("user123"), "USER", "")
        val admin = User("admin", passwordEncoder.encode("admin123"), "ADMIN", "ACCESS_TEST1,ACCESS_TEST2")
        val manager = User("manager", passwordEncoder.encode("manager123"), "MANAGER", "ACCESS_TEST1")
        val users = listOf(dan, admin, manager)

        // Save to db
        userRepository.saveAll(users)
    }

}