package com.mkundacina.pki.security

import com.mkundacina.pki.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

//@Service
//class UserPrincipalDetailsService(private val userRepository: UserRepository) : UserDetailsService {
////
////    @Throws(UsernameNotFoundException::class)
////    override fun loadUserByUsername(username: String): UserDetails =
////        UserPrincipal(userRepository.findByUsername(username))
//
//}