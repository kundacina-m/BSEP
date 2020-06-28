package com.mkundacina.pki.services

import com.mkundacina.pki.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class UserDetailsService : UserDetailsService {

    @Autowired
    lateinit var userRepository: UserRepository

    // Funkcija koja na osnovu username-a iz baze vraca objekat User-a
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails =
            userRepository.findByUserName(username)
                    ?: throw UsernameNotFoundException(String.format("No user found with username '%s'.", username))
}
