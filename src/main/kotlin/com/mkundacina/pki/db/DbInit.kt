package com.mkundacina.pki.db

import com.mkundacina.pki.model.entities.User
import com.mkundacina.pki.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class DbInit(private val userRepository: UserRepository) : CommandLineRunner {

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    override fun run(vararg args: String) {

        // Delete all
        userRepository.deleteAll()

        // Crete users
        listOf(
                User("user1", passwordEncoder.encode("user123"), "nekooo@gmail.com", true,"USER",""),
                User("admin", "\$2a\$10\$Sa91QzuYOCrx.Y1NaCJ35uV/VpSS3CyfmTApjznPfipIPsjstnzui", "email@gmail.com",true,"ADMIN", "ACCESS_TEST1,ACCESS_TEST2")
        ).run {
            // Save to db
            userRepository.saveAll(this)
        }
    }

}