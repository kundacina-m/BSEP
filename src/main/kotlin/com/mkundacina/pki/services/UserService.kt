package com.mkundacina.pki.services

import com.mkundacina.pki.model.entities.User
import com.mkundacina.pki.model.requests.RegisterRequest
import com.mkundacina.pki.repository.UserRepository
import com.mkundacina.pki.security.UserPrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    fun findByUsername(username: String) =
            userRepository.findByUserName(username)

    fun save(registerRequest: RegisterRequest) =
            userRepository.save(
                    User(
                            registerRequest.username,
                            passwordEncoder.encode(registerRequest.password),
                            registerRequest.email,
                            false,
                            "USER",
                            ""
                    )
            )

    fun enableUser(username: String) {
        val user = userRepository.findByUserName(username)
        user!!.active = true
        userRepository.save(user)
    }
}